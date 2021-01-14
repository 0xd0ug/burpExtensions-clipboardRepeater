package burp;

public class BurpExtender implements IBurpExtender
{
    public void registerExtenderCallbacks (IBurpExtenderCallbacks callbacks)
    {

        callbacks.setExtensionName ("RepeaterClips");
        callbacks.registerContextMenuFactory(new RCMenu(callbacks));
    }

    public static void main(String[] args) {

        System.out.println("RepeaterClips Burp Extension");
    }
}