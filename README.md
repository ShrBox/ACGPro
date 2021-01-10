# ACGPro

![Java CI with Gradle](https://github.com/ShrBox/ACGH/workflows/Java%20CI%20with%20Gradle/badge.svg)  
在群内随机发送二次元图片，支持关键词检索  
目前仅适配Mirai-console 0.5.2  
使用前需要先[申请API](https://api.lolicon.app/#/setu?id=apikey)

# config.yml

```
apikey: '' //填写apikey
r18: false //是否开启r18
r18-groups: //允许r18的群
- 1145141919810
limit-mode: false //限制模式，只能同时存在一个任务
FlashImageMode: true //闪照模式，发送图片为闪照
AutoRecall: 0 //自动撤回时间，0为禁用
```

# Download

[Releases](https://github.com/ShrBox/ACGPro/releases)

# Usage

在群内发送`acg [关键词] [数量(1-20)]`  
关键词中可以用`_`来表示空格  
可以不加关键词和数量  
控制台/群内输入`/acgreload`指令可以重载配置文件