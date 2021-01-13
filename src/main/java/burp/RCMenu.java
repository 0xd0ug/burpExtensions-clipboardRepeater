package burp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import org.json.JSONObject;




public class RCMenu implements IContextMenuFactory {
    final private IExtensionHelpers helpers;
    final private IBurpExtenderCallbacks callbacks;

    private byte[] compress(byte[] input) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(input);
        }
        return os.toByteArray();
    }

    private byte[] decompress(byte[] input) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(input);
        }
        return os.toByteArray();
    }

    public RCMenu(IBurpExtenderCallbacks callbacks) {
        helpers = callbacks.getHelpers();
        this.callbacks = callbacks;
    }

    public List<JMenuItem> createMenuItems(
            final IContextMenuInvocation invocation) {
        List<JMenuItem> menuItems;
        menuItems = new ArrayList<>();
        JMenuItem copyItem = new JMenuItem("Copy RepeaterClip");
        JMenuItem pasteItem = new JMenuItem("Paste RepeaterClip to Repeater");
        copyItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {

                var message = invocation.getSelectedMessages()[0];
                var request = message.getRequest();
                var service = message.getHttpService();
                // Create JSON including the protocol, port, host, and text of the request
                JSONObject requestJson = new JSONObject();
                requestJson.put("protocol", service.getProtocol());
                requestJson.put("host", service.getHost());
                requestJson.put("port", service.getPort());
                // Base64-encode the request to make sure it's "safe".
                requestJson.put("request", new String(request));
                String jsonString = requestJson.toString();
                byte[] jsonBytes = jsonString.getBytes();
                // Put the JSON on the clipboard as a string.
                // Base64-encode the JSON to make sure email/IM doesn't impact the format.
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = null;
                try {
                    stringSelection = new StringSelection(helpers.base64Encode(compress(jsonBytes)));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                clipboard.setContents(stringSelection, null);
            }


            public void mouseEntered(MouseEvent e) {}


            public void mouseExited(MouseEvent e) {}
        });

        pasteItem.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {
                String clipText = null;
                // Get the clipboard contents and make sure it's text.
                try {
                    clipText = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException unsupportedFlavorException) {
                    callbacks.issueAlert("Clipboard did not contain string. Unable to create repeater.");
                }

                byte[] decoded = new byte[0];
                try {
                    decoded = decompress(helpers.base64Decode(clipText));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                JSONObject jsonDecoded = new JSONObject(new String(decoded));
                String host = jsonDecoded.getString("host");
                int port = jsonDecoded.getInt("port");
                String protocol = jsonDecoded.getString("protocol");
                boolean useHttps = false;
                if (protocol.equals("https")) {
                    useHttps=true;
                }
                byte[] request = jsonDecoded.getString("request").getBytes();
                callbacks.sendToRepeater(host,port,useHttps,request,null);
            }


            public void mouseEntered(MouseEvent e) {}


            public void mouseExited(MouseEvent e) {}
            
        });

        menuItems.add(copyItem);
        menuItems.add(pasteItem);
        return menuItems;
    }
}

