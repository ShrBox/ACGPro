package shrbox.github.acgpro;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class Main extends PluginBase {
    public static Config config;

    short pullCount = 0;
    public static short threadRunning = 0;

    short maxPullCount = 10;
    public static boolean flashImageMode = false;
    public static int autoRecall = 0;
    short maxThread = 10;
    public static boolean originalImages = false;

    public void loadConfig() { //配置文件加载
        config = loadConfig("config.yml");

        config.setIfAbsent("apikey", "");
        config.setIfAbsent("r18", false);
        config.setIfAbsent("MaxPullCount", 10); //每分钟最多请求的次数
        config.setIfAbsent("FlashImageMode", false); //闪照模式
        config.setIfAbsent("AutoRecall", 0); //自动撤回
        config.setIfAbsent("MaxThread", 10); //最大线程数
        config.setIfAbsent("OriginalImages", false); //原图模式

        List<Long> r18Groups = new ArrayList<>();
        Collections.addAll(r18Groups, 1145141919L, 123123123L);
        config.setIfAbsent("r18-groups", r18Groups);
        r18Groups.clear();

        config.save();

        maxPullCount = (short) config.getInt("MaxPullCount");
        flashImageMode = config.getBoolean("FlashImageMode");
        autoRecall = config.getInt("AutoRecall");
        maxThread = (short) config.getInt("MaxThread");
        originalImages = config.getBoolean("OriginalImages");
    }

    public void registerCommands() {
        JCommandManager.getInstance().register(this, new BlockingCommand( //注册命令
                "acgreload", new ArrayList<>(), "重载ACGPro配置文件", "/acghreload"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                loadConfig();
                commandSender.sendMessageBlocking("重载成功");
                return true;
            }
        });
    }

    public void onEnable() {
        loadConfig();
        registerCommands();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pullCount = 0;
            }
        }, 60 * 1000, 60 * 1000); //一分钟自动重置变量

        System.setProperty("http.agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)");

        getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent e) -> {//监听群消息
            if (e.getMessage().contentToString().toLowerCase().contains("acg")) {
                if (e.getGroup().getBotMuteRemaining() > 0) return;
                if (pullCount > maxPullCount || threadRunning >= maxThread) {
                    e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender())).plus("[ACGPro] 请先喝口水再尝试"));
                    return;
                }
                pullCount++;
                new Thread().newThread(e);
            }
        });
    }
}