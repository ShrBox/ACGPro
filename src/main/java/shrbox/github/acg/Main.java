package shrbox.github.acg;

import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.GroupMessageEvent;

class Main extends PluginBase {
    public void onEnable() {
        System.setProperty("http.agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)");
        getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent e) -> {
           if (e.getMessage().contentToString().toLowerCase().contains("acg")) {
                NewThread newThread = new NewThread();
                newThread.boot(e);
           }
        });
    }
}