package shrbox.github.acgpro;

import com.google.gson.Gson;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.URL;
import java.util.List;
import java.util.Random;

public class Thread extends java.lang.Thread {
    GroupMessageEvent e;
    public void boot(GroupMessageEvent event) {
        this.e = event;
        start();
    }

    @Override
    public void run() {
        if (Main.ispulling) {
            e.getGroup().sendMessage("[ACGPro] 正在从拉取图片，请稍后再试");
            return;
        }
        String message = e.getMessage().contentToString();
        String msgcontent = message.toLowerCase().replace("acg", "").trim();
        String keyword = msgcontent;
        short picnum = 1;
        if (msgcontent.contains(" ")) {
            String[] str = msgcontent.split(" ", 2);
            keyword = str[0];
            if (str[1] == null) return;
            char[] picnum_ = str[1].toCharArray();
            if (!Character.isDigit(picnum_[0])) return;
            picnum = (short) Integer.parseInt(str[1]);
        }
        Config config = Main.config;
        List<Long> r18_groups = config.getLongList("r18-groups");
        boolean isr18 = false;
        if (config.getBoolean("r18") && r18_groups.contains(e.getGroup().getId())) isr18 = true;
        String content = Connection.getURL(keyword, isr18);
        if (content.equals("")) {
            e.getGroup().sendMessage("无法访问到接口");
            return;
        }
        Gson gson = new Gson();
        Json_pre json_pre = gson.fromJson(content, Json_pre.class);
        int return_code = json_pre.code;
        if (return_code != 0) {
            e.getGroup().sendMessage(json_pre.msg);
            return;
        }
        Json json = gson.fromJson(content, Json.class);
        if (picnum == 1) {
            Random random = new Random();
            short index = (short) random.nextInt(json.data.size());
            Data data = json.data.get(index);
            e.getGroup().sendMessage("[ACGPro] 正在从服务器拉取图片...");
            sendpic(data);
        } else {
            if (picnum > json.data.size()) picnum = (short) json.data.size();
            e.getGroup().sendMessage("[ACGPro] 正在从服务器拉取" + picnum + "张图片...");
            for (short a = 0; a < picnum; a++) {
                if (a == json.data.size()) break;
                Data data = json.data.get(a);
                Image image = null;
                Main.ispulling = true;
                try {
                    image = e.getGroup().uploadImage(new URL(data.url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (image == null) {
                    e.getGroup().sendMessage("[ACGPro] 有一张图片解析错误");
                    Main.ispulling = false;
                    continue;
                }
                e.getGroup().sendMessage(MessageUtils.newChain(image)
                        .plus("作品标题: " + data.title + "\nPid: " + data.pid + "\n作者名: " + data.author + "\n作者UID: " + data.uid));
            }
        }
        Main.ispulling = false;
    }

    private void sendpic(Data data) {
        Image image = null;
        Main.ispulling = true;
        try {
            image = e.getGroup().uploadImage(new URL(data.url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (image == null) {
            e.getGroup().sendMessage("[ACGPro] 图片解析错误");
            Main.ispulling = false;
            return;
        }
        e.getGroup().sendMessage(MessageUtils.newChain(image)
                .plus("作品标题: " + data.title + "\nPid: " + data.pid + "\n作者名: " + data.author + "\n作者UID: " + data.uid));
    }
}