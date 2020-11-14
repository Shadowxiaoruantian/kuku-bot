package me.kuku.yuq.logic.impl;

import com.IceCreamQAQ.Yu.util.OkHttpWebImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icecreamqaq.yuq.mirai.MiraiBot;
import me.kuku.yuq.logic.QQGroupLogic;
import me.kuku.yuq.pojo.GroupMember;
import me.kuku.yuq.pojo.Result;
import me.kuku.yuq.pojo.UA;
import me.kuku.yuq.utils.BotUtils;
import me.kuku.yuq.utils.OkHttpUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QQGroupLogicImpl implements QQGroupLogic {
    @Inject
    private OkHttpWebImpl web;
    @Inject
    private MiraiBot miraiBot;

    @Override
    public String addGroupMember(Long qq, Long group) {
        HashMap<String, String> map = new HashMap<>();
        map.put("gc", String.valueOf(group));
        map.put("ul", String.valueOf(qq));
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/add_group_member", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("ec")){
            case 0: return "邀请" + qq + "成功";
            case 4: return "邀请失败，请更新QQ！！";
            default: return "邀请失败，" + jsonObject.getString("em");
        }
    }

    @Override
    public String setGroupAdmin(Long qq, Long group, boolean isAdmin) {
        Map<String, String> map = new HashMap<>();
        map.put("gc", String.valueOf(group));
        map.put("ul", group.toString());
        int op = 0;
        if (isAdmin) op = 1;
        map.put("op", String.valueOf(op));
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/set_group_admin", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("ec")){
            case 0: return String.format("设置%d为管理员成功", qq);
            case 4: return "设置失败，请更新QQ！！";
            case 14:
            case 3: return "设置失败，没有权限！！；";
            default: return "设置失败，" + jsonObject.getString("em");
        }
    }

    @Override
    public String setGroupCard(Long qq, Long group, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("gc", group.toString());
        map.put("ul", qq.toString());
        map.put("name", name);
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/set_group_card", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("ec")){
            case 0: return String.format("更改%d名片为%s成功", qq, name);
            case 4: return "更改名片失败，请更新qq";
            case 14:
            case 3: return "更改名片失败，没有权限";
            default: return "更改名片失败，" + jsonObject.getString("em");
        }
    }

    @Override
    public String deleteGroupMember(Long qq, Long group, boolean isFlag) {
        Map<String, String> map = new HashMap<>();
        map.put("gc", group.toString());
        map.put("ul", qq.toString());
        int isFlagNum = 0;
        if (isFlag) isFlagNum = 1;
        map.put("name", String.valueOf(isFlagNum));
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/delete_group_member", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("ec")){
            case 0: return String.format("踢%d成功", qq);
            case 4: return "踢人失败，请更新QQ！！";
            case 14:
            case 3: return "踢人失败，没有权限";
            default: return "踢人失败，" + jsonObject.getString("em");
        }
    }

    @Override
    public String addHomeWork(Long group, String courseName, String title, String content, boolean needFeedback) {
        Map<String, String> map = new HashMap<>();
        map.put("homework_id", "");
        map.put("group_id", group.toString());
        map.put("course_id", "2");
        map.put("course_name", courseName);
        map.put("title", title);
        int num = 0;
        if (needFeedback) num = 1;
        map.put("need_feedback", String.valueOf(num));
        map.put("c", String.format("{\"c\":[{\"type\":\"str\",\"text\":\"%s\"}]}", content));
        map.put("team_id", "0");
        map.put("hw_type", "0");
        map.put("tsfeedback", "");
        map.put("syncgids", "");
        map.put("client_type", "1");
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/homework/hw/assign_hw.fcg", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("retcode")){
            case 0: return "发布作业成功！！";
            case 110002: return "权限不足，无法发布作业！！";
            case 100000: return "发布失败，请更新QQ！！";
            default: return jsonObject.getString("msg");
        }
    }

    @Override
    public String groupCharin(Long group, String content, Long time) {
        Map<String, String> map = new HashMap<>();
        map.put("gc", group.toString());
        map.put("desc", content);
        map.put("type", "2");
        map.put("expired", time.toString());
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/group_chain/chain_new", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("rt")){
            case 0: return "发布群接龙成功！！";
            case 11004: return "到期时间格式有误";
            case 10013: return "权限不足，无法发布群接龙！！";
            case 100000: return "发布失败，请更新QQ！！";
            default: return jsonObject.getString("msg");
        }
    }

    @Override
    public Result<List<Map<String, String>>> groupLevel(Long group) {
        String str = web.get("https://qun.qq.com/interactive/levellist?gc=$group&type=7&_wv=3&_wwv=128");
        String jsonStr = BotUtils.regex("window.__INITIAL_STATE__=", "</script>", str);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("membersList");
        if (jsonArray.size() == 0) return Result.failure("获取群等级列表失败，请更新QQ！！", null);
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject singleJsonObject = jsonArray.getJSONObject(i);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("name", singleJsonObject.getString("name"));
            resultMap.put("level", singleJsonObject.getString("level"));
            resultMap.put("tag", singleJsonObject.getString("tag"));
            resultMap.put("qq", singleJsonObject.getString("uin"));
            list.add(resultMap);
        }
        return Result.success(list);
    }

    @Override
    public Result<GroupMember> queryMemberInfo(Long group, Long qq) {
        Map<String, String> map = new HashMap<>();
        map.put("gc", group.toString());
        map.put("st", "0");
        map.put("end", "20");
        map.put("sort", "0");
        map.put("key", qq.toString());
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/search_group_members", map);
        JSONObject jsonObject = JSON.parseObject(str);
        switch (jsonObject.getInteger("ec")){
            case 0: {
                JSONArray jsonArray = jsonObject.getJSONArray("mems");
                if (jsonArray == null || jsonArray.size() == 0) return Result.failure("为搜索到该用户", null);
                JSONObject memberJsonObject = jsonArray.getJSONObject(0);
                String card = memberJsonObject.getString("card");
                String name;
                if ("".equals(card)) name = memberJsonObject.getString("nick");
                else name = card;
                return Result.success(new GroupMember(
                        memberJsonObject.getLong("uin"),
                        Long.parseLong(memberJsonObject.getString("join_time") + "000"),
                        Long.parseLong((memberJsonObject.getString("last_speak_time") + "000")),
                        memberJsonObject.getInteger("qage"),
                        name
                ));
            }
            case 4: return Result.failure("查询失败请更新QQ！！", null);
            default: return Result.failure(jsonObject.getString("em"), null);
        }
    }

    @Override
    public Result<List<String>> essenceMessage(Long group) {
        String html = web.get("https://qun.qq.com/essence/index?gc=$group&_wv=3&_wwv=128&_wvx=2&_wvxBclr=f5f6fa");
        String jsonStr = BotUtils.regex("window.__INITIAL_STATE__=", "</", html);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("msgList");
        if (jsonArray.size() == 0) return Result.failure("当前群内没有精华消息！！或者cookie已失效！！", null);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject msgJsonObject = jsonArray.getJSONObject(i);
            JSONArray contentJsonArray = msgJsonObject.getJSONArray("msg_content");
            StringBuilder sb = new StringBuilder();
            contentJsonArray.forEach(obj -> {
                JSONObject singleJsonObject = (JSONObject) obj;
                String text = singleJsonObject.getString("text");
                if (text != null) sb.append(text);
                else sb.append(singleJsonObject.getString("face_text"));
            });
            list.add(sb.toString());
        }
        return Result.success(list);
    }

    @Override
    public Result<List<Long>> queryGroup() {
        Map<String, String> map = new HashMap<>();
        map.put("bkn", String.valueOf(miraiBot.getGtk()));
        String str = web.post("https://qun.qq.com/cgi-bin/qun_mgr/get_group_list", map);
        JSONObject jsonObject = JSON.parseObject(str);
        if (jsonObject.getInteger("ec").equals(0)){
            List<Long> list = new ArrayList<>();
            JSONArray manageJsonArray = jsonObject.getJSONArray("manage");
            manageJsonArray.forEach(obj -> {
                JSONObject groupJsonObject = (JSONObject) obj;
                list.add(groupJsonObject.getLong("gc"));
            });
            JSONArray joinJsonArray = jsonObject.getJSONArray("join");
            joinJsonArray.forEach(obj -> {
                JSONObject groupJsonObject = (JSONObject) obj;
                list.add(groupJsonObject.getLong("gc"));
            });
            JSONArray createJsonArray = jsonObject.getJSONArray("create");
            createJsonArray.forEach(obj -> {
                JSONObject groupJsonObject = (JSONObject) obj;
                list.add(groupJsonObject.getLong("gc"));
            });
            return Result.success(list);
        }else return Result.failure("查询失败，请更新qun.qq.com的cookie", null);
    }

    @Override
    public List<Map<String, String>> groupHonor(Long group, String type) {
        int typeNum;
        int wwv;
        String param;
        String image;
        List<Map<String, String>> list = new ArrayList<>();
        switch (type){
            case "talkAtIve": {
                typeNum = 1;
                wwv = 129;
                param = "talkativeList";
                image = "https://qq-web.cdn-go.cn/qun.qq.com_interactive/067dafcc/app/qunhonor/dist/cdn/assets/images/icon-drgon.png";
                break;
            }
            case "legend": {
                typeNum = 3;
                wwv = 128;
                param = "legendList";
                image = "https://qq-web.cdn-go.cn/qun.qq.com_interactive/067dafcc/app/qunhonor/dist/cdn/assets/images/icon-fire-big.png";
                break;
            }
            case "actor": {
                typeNum = 2;
                wwv = 128;
                param = "actorList";
                image = "https://qq-web.cdn-go.cn/qun.qq.com_interactive/067dafcc/app/qunhonor/dist/cdn/assets/images/icon-fire-small.png";
                break;
            }
            case "strongNewBie": {
                typeNum = 5;
                wwv = 128;
                param = "strongnewbieList";
                image = "https://qq-web.cdn-go.cn/qun.qq.com_interactive/067dafcc/app/qunhonor/dist/cdn/assets/images/icon-shoots-small.png";
                break;
            }
            case "emotion": {
                typeNum = 6;
                wwv = 128;
                param = "emotionList";
                image = "https://qq-web.cdn-go.cn/qun.qq.com_interactive/067dafcc/app/qunhonor/dist/cdn/assets/images/icon-happy-stream.png";
                break;
            }
            default: return list;
        }
        String html = web.get(String.format("https://qun.qq.com/interactive/honorlist?gc=%d&type=%d&_wv=3&_wwv=%d", group, typeNum, wwv));
        String jsonStr = BotUtils.regex("window.__INITIAL_STATE__=", "</script", html);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray(param);
        jsonArray.forEach(obj -> {
            JSONObject singleJsonObject = (JSONObject) obj;
            Map<String, String> map = new HashMap<>();
            map.put("qq", singleJsonObject.getString("uin"));
            map.put("name", singleJsonObject.getString("name"));
            map.put("desc", singleJsonObject.getString("desc"));
            map.put("image", image);
            list.add(map);
        });
        return list;
    }

    @Override
    public Result<String> groupSign(Long group, String place, String text, String name, String picId, String picUrl) {
        String gtk = String.valueOf(miraiBot.getGtk());
        String qq= miraiBot.qq;
        String info = null;
        String templateId = null;
        String templateStr = web.get(String.format("https://qun.qq.com/cgi-bin/qiandao/gallery_template?gc=%d&bkn=%s&time=1014", group, gtk));
        JSONObject templateJsonObject = JSON.parseObject(templateStr);
        if (templateJsonObject.getInteger("retcode") != 0) return Result.failure("qq群签到失败，请更新QQ！！", null);
        out:for (Object obj: templateJsonObject.getJSONObject("data").getJSONArray("list")){
            JSONObject singleJsonObject = (JSONObject) obj;
            if (name.equals(singleJsonObject.getString("name"))){
                if (singleJsonObject.containsKey("gallery_info")){
                    JSONObject infoJsonObject = singleJsonObject.getJSONObject("gallery_info");
                    picUrl = infoJsonObject.getString("url");
                    info = String.format("{\"category_id\":%s,\"page\":%s,\"pic_id\":%s}",
                            infoJsonObject.getInteger("category_id"), infoJsonObject.getInteger("page"),
                            infoJsonObject.getInteger("pic_id"));
                }else info = "{\"category_id\":\"\",\"page\":\"\",\"pic_id\":\"\"}";
                templateId = singleJsonObject.getString("id");
                break;
            }
            if ("自定义".equals(singleJsonObject.getString("name"))) break;
            String idStr = web.get(String.format("https://qun.qq.com/cgi-bin/qiandao/gallery_list/category?template_id=%s&bkn=%s&need_dynamic=1",
                    singleJsonObject.getInteger("id"), gtk));
            JSONObject idJsonObject = JSON.parseObject(idStr);
            List<String> param = new ArrayList<>();
            for (Object id: idJsonObject.getJSONObject("data").getJSONArray("category_list")){
                JSONObject singleIdJsonObject = (JSONObject) id;
                param.add(singleIdJsonObject.getString("category_id"));
            }
            String deepStr = web.get(String.format("https://qun.qq.com/cgi-bin/qiandao/gallery_list?bkn=%s&category_ids=%s&start=0&num=50",
                    gtk, param));
            JSONObject deepJsonObject = JSON.parseObject(deepStr);
            for (Object sin: deepJsonObject.getJSONObject("data").getJSONArray("picture_list")){
                JSONObject jsonObject = (JSONObject) sin;
                for (Object item: jsonObject.getJSONArray("picture_item")){
                    JSONObject itemJsonObject = (JSONObject) item;
                    if (name.equals(itemJsonObject.getString("name"))){
                        info = String.format("{\"category_id\":\"%s\",\"page\":\"%s\",\"pic_id\":\"%s\"}",
                                jsonObject.getInteger("category_id"), itemJsonObject.getInteger("page"),
                                itemJsonObject.getInteger("picture_id"));
                        templateId = singleJsonObject.getString("id");
                        picUrl = itemJsonObject.getString("url");
                        break out;
                    }
                }

            }
        }
        if (info == null || templateId == null){
            String template2Str = web.get(String.format("https://qun.qq.com/cgi-bin/qiandao/gallery_list?bkn=%s&category_ids=[9]&start=0&num=50", gtk));
            JSONObject template2JsonObject = JSON.parseObject(template2Str);
            JSONObject picJsonObject = template2JsonObject.getJSONObject("data").getJSONArray("picture_list").getJSONObject(0);
            for (Object obj: picJsonObject.getJSONArray("picture_item")){
                JSONObject singleJsonObject = (JSONObject) obj;
                if (name.equals(singleJsonObject.getString("name"))){
                    info = String.format("{\"category_id\":%s,\"page\":%s,\"pic_id\":%s}",
                            picJsonObject.getInteger("category_id"), singleJsonObject.getInteger("page"), singleJsonObject.getInteger("picture_id"));
                    templateId = "[object Object]";
                    picUrl = singleJsonObject.getString("url");
                    break;
                }
            }
        }

        if (info == null || templateId == null) return Result.failure("群签到类型中没有" + name + "这个类型，请重试！！", null);
        Map<String, String> map = new HashMap<>();
        map.put("btn", gtk);
        map.put("template_data", "");
        map.put("gallery_info", info);
        map.put("template_id", templateId);
        map.put("gc", group.toString());
        map.put("client", "2");
        map.put("lgt", "0");
        map.put("lat", "0");
        map.put("poi", place);
        map.put("text", text);
        if (picId == null) picId = "";
        map.put("pic_id", picId);
        JSONObject jsonObject = null;
        try {
            jsonObject = OkHttpUtils.postJson("https://qun.qq.com/cgi-bin/qiandao/sign/publish", map,
                    OkHttpUtils.addHeaders(BotUtils.toQQLoginEntity(web, miraiBot).getCookieWithGroup(), null, UA.QQ2));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure("出现异常了！！", null);
        }
        switch (jsonObject.getInteger("retcode")){
            case 0: {
                String resultStr = web.get(String.format("https://qun.qq.com/cgi-bin/qiandao/list?gc=%s&uin=%s&type=0&num=10&sign_id=&bkn=%s", group, qq, gtk));
                JSONObject resultJsonObject = JSON.parseObject(resultStr);
                String id = resultJsonObject.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("sign_id");
                return Result.success("{\"app\":\"com.tencent.qq.checkin\",\"desc\":\"群签到\",\"view\":\"checkIn\",\"ver\":\"1.0.0.25\",\"prompt\":\"[群签到]群签到\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"sourceUrl\":\"\",\"meta\":{\"checkInData\":{\"address\":\"" + place + "\",\"cover\":{\"height\":0,\"url\":\"" + picUrl + "\",\"width\":0},\"desc\":\"" + text + "\",\"hostuin\":" + qq + ",\"id\":\"" + id + "\",\"media_type\":0,\"qunid\":\"" + group + "\",\"rank\":1,\"skip_to\":1,\"time\":0,\"url\":\"mqqapi:\\/\\/microapp\\/open?appid=1108164955&path=pages%2Fchecklist%2Fchecklist&extraData=929630359%7C" + id + "\",\"vid\":\"\"}},\"config\":{\"forward\":0,\"showSender\":1},\"text\":\"\",\"sourceAd\":\"\",\"extra\":\"\"}");
            }
            case 10013:
            case 10001: return Result.failure("qq群签到失败，已被禁言！", null);
            case 10016: return Result.failure("群签到一次性只能签到5个群，请10分钟后再试！");
            case 5: return Result.failure("qq群签到失败，请更新QQ！！");
            default: return Result.failure("qq群签到失败，" + jsonObject.getString("msg"), null);
        }
    }
}