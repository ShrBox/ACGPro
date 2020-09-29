package shrbox.github.acg;

import com.google.gson.Gson;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class Thread extends java.lang.Thread {
    GroupMessageEvent e;
    public void boot(GroupMessageEvent event) {
        this.e = event;
        start();
    }

    @Override
    public void run() {
        String message = e.getMessage().contentToString();
        String keyword = message.toLowerCase().replace("acg", "").trim();
        String content = Connection.getURL(keyword);
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
        Image image = null;
        Random random = new Random();
        int index = random.nextInt(json.data.size());
        Data data = json.data.get(index);
        e.getGroup().sendMessage("[ACGHPro] 正在从服务器拉取图片...");
        try {
            image = e.getGroup().uploadImage(new URL(data.url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (image == null) {
            e.getGroup().sendMessage("图片解析错误");
            return;
        }
        e.getGroup().sendMessage(MessageUtils.newChain(image)
                .plus("作品标题: " + data.title + "\npid: " + data.pid + " p: "
                        + data.p + "\n作者名: " + data.author + "\n作者UID: " + data.uid
                        + "\n原图分辨率: " + data.width + " x " + data.height + "\ntags: "
                        + Arrays.toString(data.tags.toArray())));
    }
}
