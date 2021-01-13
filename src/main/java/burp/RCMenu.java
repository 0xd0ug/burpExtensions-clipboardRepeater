package burp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;




public class RCMenu implements IContextMenuFactory {
    final private IExtensionHelpers helpers;
    final private IBurpExtenderCallbacks callbacks;

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
                requestJson.put("request", helpers.base64Encode(request));
                String jsonString = requestJson.toString();
                // Put the JSON on the clipboard as a string.
                // Base64-encode the JSON to make sure email/IM doesn't impact the format.
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(helpers.base64Encode(jsonString));
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
                decoded = helpers.base64Decode(clipText);

                JSONObject jsonDecoded = new JSONObject(new String(decoded));
                String host = jsonDecoded.getString("host");
                int port = jsonDecoded.getInt("port");
                String protocol = jsonDecoded.getString("protocol");
                boolean useHttps = false;
                if (protocol.equals("https")) {
                    useHttps=true;
                }
                byte[] request = helpers.base64Decode(jsonDecoded.getString("request"));
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

