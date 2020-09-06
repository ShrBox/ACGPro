package shrbox.github.acg;

import com.google.gson.Gson;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.URL;

public class NewThread extends Thread {
    GroupMessageEvent e;

    public void boot(GroupMessageEvent event) {
        this.e = event;
        start();
    }

    @Override
    public void run() {
        String json = Connection.getURL();
        if (json.equals("")) {
            e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender())).plus("接口错误（0）"));
            return;
        }
        Gson gson = new Gson();
        Json json1 = gson.fromJson(json, Json.class);
        if (!json1.code.equals("200")) {
            e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender())).plus("接口错误（1）"));
            return;
        }
        Image image = null;
        try {
            image = e.getGroup().uploadImage(new URL(json1.imgurl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender()))
                .plus("\nSize: " + json1.width + "*" + json1.height)
                .plus(image)
                .plus("ImageURL: " + json1.imgurl)
        );
    }
}
