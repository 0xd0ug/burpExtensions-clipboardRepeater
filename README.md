# RepeaterClips

RepeaterClips is my first attempt at a Burp Extension. Have you ever tried to share a request through email or IM, only to have your desktop client modify (mess up) the request? Maybe it randomly creates emoticons or puts a bunch of weird spaces in the headers. RepeaterClips is here to solve that problem.

## How it works

Once RepeaterClips successfully loads, it creates two new items in Burp's context menu for requests. The first, **Copy RepeaterClip**, reads the protocol (http or https), host address, port number, and text from the current request; consolidates it into a JSON string; compresses the string with `java.util.zip.DeflaterOutputStream`; encodes it with Base64, and copies it to your system clipboard. As you might expect, the other menu item, **Paste RepeaterClip to Repeater**, creates a new Repeater tab and generates a request based on a RepeaterClip you have hopefully copied to the clipboard.

So if you want to share a request with a colleague, just choose the **Copy RepeaterClip** option, transition to your favorite email or IM client, and paste the clipboard into a message to send. Your colleague will then copy the Base64 text string you pasted, transition to Burp, and then choose **Paste RepeaterClip to Repeater**. They will then see a new Repeater tab with a request identical to the one on your system. If they identify a new attack vector and want to send their modified request back to you, they simply reverse the process.

As a side effect (and as a great way to test this extension), choosing **Copy Repeater Clip** and then immediately choosing **Paste RepeaterClip to Repeater** has the same effect as the standard Burp menu item **Send  to Repeater**. If this doesn't work, there may be something wrong with the extension.

## Building

You can build RepeaterClips with maven. This is my first time ever using maven, so caveat emptor! I also built a jar with dependencies and included it in the Target directory. This jar worked on both Windows and Mac for me.

## Important Considerations

Requests can contain session cookies or even encoded or cleartext passwords in headers or parameters. **_RepeaterClips does not encrypt any data._** Though each RepeaterClip appears obfuscated, it is just compressed and encoded with Base64 using standard libraries. If sensitive data is contained in the request, you'll need to take appropriate steps to protect it yourself.

Be aware that if, for some reason, the extension cannot convert your request, the clipboard may not change. Make sure you verify the contents (perhaps by choosing the **Paste RepeaterClip to Repeater** option) before you paste something you didn't intend to paste and send it to a colleague by mistake.

Likewise, RepeaterClips doesn't verify the integrity of requests or check them for malicious content. Be sure you trust your source and the integrity of your messaging channel prior to clicking that "Send" button in Burp Repeater after pasting a RepeaterClip.

There are many great extensions in the BApp store, some of which are designed to aid in collaboration. I created RepeaterClips because my colleagues and I needed a quick way to send requests to each other, and because this method worked best for us. I encourage you to check out other great extensions and use the one that works best for you. And if you have any ideas to improve RepeaterClips, please create an Issue here or tweet at me @0xd0ug on Twitter. I don't have a lot of time to spend on development, but I'll do what I can. Thanks!


