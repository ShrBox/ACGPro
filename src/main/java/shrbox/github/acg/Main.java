package shrbox.github.acg;

import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.GroupMessageEvent;

class Main extends PluginBase {
    public void onEnable() {
        getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent e) -> {
           if (e.getMessage().contentToString().toLowerCase().contains("acg")) {
                NewThread newThread = new NewThread();
                newThread.boot(e);
           }
        });
    }
}        