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
    public static boolean isPulling = false;
    short count = 0;
    public static boolean flashImageMode;
    public static int autoRecall;

    public void load_Config() { //配置文件加载
        config = loadConfig("config.yml");
        config.setIfAbsent("apikey", "");
        config.setIfAbsent("r18", false);
        List<Long> r18Groups = new ArrayList<>();
        Collections.addAll(r18Groups, 1145141919L, 123123123L);
        config.setIfAbsent("r18-groups", r18Groups);
        config.setIfAbsent("limit-mode", false);
        config.setIfAbsent("FlashImageMode", false);
        config.setIfAbsent("AutoRecall", 0);
        config.save();
        r18Groups.clear();
        flashImageMode = config.getBoolean("FlashImageMode");
        autoRecall = config.getInt("AutoRecall");
    }
    public void onEnable() {
        load_Config();
        JCommandManager.getInstance().register(this, new BlockingCommand( //注册命令
                "acgreload", new ArrayList<>(), "重载ACGPro配置文件", "/acghreload"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                load_Config();
                commandSender.sendMessageBlocking("重载成功");
                return true;
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                count = 0;
            }
        }, 60 * 1000, 60 * 1000); //一分钟自动重置变量count
        System.setProperty("http.agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)");
        getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent e) -> {//监听群消息
            if (e.getMessage().contentToString().toLowerCase().contains("acg")) {
                if (config.getBoolean("limit-mode") && Main.isPulling) {//如果limit-mode为true，则同时只能存在一个图片下载任务
                    e.getGroup().sendMessage("[ACGPro] 正在下载图片，请稍后再试");
                    return;
                }
                if (count > 15) {
                    e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender())).plus("[ACGPro] 请先喝口水再尝试"));
                    return;
                }
                count++;
                new Thread().boot(e);
            }
        });
    }
}