package shrbox.github.acgpro;

import com.google.gson.Gson;
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
        this.start();
    }

    @Override
    public void run() {
        String msgContent = e.getMessage().contentToString().toLowerCase().replace("acg", "").trim();
        short pigNum = 1;
        if (msgContent.contains(" ")) {
            String[] str = msgContent.split(" ", 2);
            msgContent = str[0];
            if (str[1] == null) return;
            char[] pigNum_ = str[1].toCharArray();
            if (!Character.isDigit(pigNum_[0])) return;
            pigNum = Short.parseShort(str[1]);
        }
        List<Long> r18Groups = Main.config.getLongList("r18-groups");
        boolean isR18 = false;
        if (Main.config.getBoolean("r18") && r18Groups.contains(e.getGroup().getId())) isR18 = true;
        String content = Connection.getURL(msgContent, isR18);
        if (content.equals("")) {
            e.getGroup().sendMessage("无法访问到接口");
            return;
        }
        Gson gson = new Gson();
        JsonPre jsonPre = gson.fromJson(content, JsonPre.class);
        String errorMsg = null;
        if (jsonPre.code != 0) {
            switch (jsonPre.code) {
                case -1:
                    errorMsg = "内部错误";
                    break;
                case 401:
                    errorMsg = "apikey不存在或被封禁";
                    break;
                case 403:
                    errorMsg = "由于不规范操作拒绝调用";
                    break;
                case 404:
                    errorMsg = "找不到符合关键词的图片";
                    break;
                case 429:
                    errorMsg = "达到调用额度限制";
            }
            e.getGroup().sendMessage("[ACGPro] " + errorMsg);
            return;
        }
        Json json = gson.fromJson(content, Json.class);
        /*
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                stop();
                Main.ispulling = false;
                e.getGroup().sendMessage("[ACGPro] 请求超时");
                this.cancel();
            }
        };
        new Timer().schedule(timerTask, 80 * 1000);
        */
        if (pigNum == 1) {
            Random random = new Random();
            short index = (short) random.nextInt(json.data.size());
            Data data = json.data.get(index);
            e.getGroup().sendMessage("[ACGPro] 正在从服务器下载图片...");
            Image image = null;
            Main.isPulling = true;
            try {
                image = e.getGroup().uploadImage(new URL(data.url.replace("i.pixiv.cat", "pixivi.sakuralo.top")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (image == null) {
                e.getGroup().sendMessage("[ACGPro] 图片下载错误");
                Main.isPulling = false;
                return;
            }
            e.getGroup().sendMessage(MessageUtils.newChain(image)
                    .plus("作品标题: " + data.title + "\nPid: " + data.pid + "\n作者名: " + data.author + "\n作者UID: " + data.uid));
        } else {
            if (pigNum > json.data.size()) pigNum = (short) json.data.size();
            e.getGroup().sendMessage("[ACGPro] 正在从服务器下载" + pigNum + "张图片...");
            for (short a = 0; a < pigNum; a++) {
                Data data = json.data.get(a);
                Image image = null;
                Main.isPulling = true;
                try {
                    image = e.getGroup().uploadImage(new URL(data.url.replace("i.pixiv.cat", "pixivi.sakuralo.top")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (image == null) {
                    e.getGroup().sendMessage("[ACGPro] 图片下载错误");
                    Main.isPulling = false;
                    continue;
                }
                e.getGroup().sendMessage(MessageUtils.newChain(image)
                        .plus("作品标题: " + data.title + "\nPid: "
                                + data.pid + "\n作者名: " + data.author
                                + "\n作者UID: " + data.uid
                                + "\n[" + (a + 1) + "/" + pigNum + "]"));
            }
        }
        //timerTask.cancel();
        Main.isPulling = false;
    }
}