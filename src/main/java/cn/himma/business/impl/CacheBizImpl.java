package cn.himma.business.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import cn.himma.business.ICacheBiz;
import cn.himma.entity.MenuEntity;

@Service
public class CacheBizImpl implements ICacheBiz {
    private List<MenuEntity> cacheMenuList;

    @Override
    public List<MenuEntity> getCacheMenuList() {
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            initCacheMenuList();
        }
        return new ArrayList<MenuEntity>(cacheMenuList);
    }

    // 接口
    @Override
    public void addMenu(MenuEntity toAdd) {
        // 1.先向数据库添加
        // TODO
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            initCacheMenuList();
        } else {
            // 2.再向缓存添加，这样可不用再查库
            if (toAdd.getCode().contains("/")) {
                addChildMenu(toAdd);
            } else {
                addParentMenu(toAdd);
            }
            // cacheMenuList.add(new MenuEntity(key, value));
        }
    }

    private void addParentMenu(MenuEntity toAdd) {
        for (int i = 0; i < cacheMenuList.size(); i++) {
            if (cacheMenuList.get(i).getSeq() > toAdd.getSeq()) {
                cacheMenuList.add(i, toAdd);
                break;
            }
        }
    }

    private void addChildMenu(MenuEntity toAdd) {
        for (int i = 0; i < cacheMenuList.size(); i++) {
            if (toAdd.getCode().split("/")[0].equals(cacheMenuList.get(i).getCode())) {
                List<MenuEntity> children = cacheMenuList.get(i).getChildren();
                for (int j = 0; j < children.size(); j++) {
                    if (children.get(j).getSeq() > toAdd.getSeq()) {
                        children.add(j, toAdd);
                        break;
                    }
                }
            }
        }
    }

    public void initCacheMenuList() {
        // 模拟查库过程
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            cacheMenuList = new ArrayList<MenuEntity>();
            cacheMenuList.add(new MenuEntity("index", "首页", 0, new ArrayList<MenuEntity>()));

            MenuEntity job = new MenuEntity("job", "搬砖区", 1, new ArrayList<MenuEntity>());
            job.getChildren().add(new MenuEntity("job/soft", "IT迪奥斯", 0, null));
            job.getChildren().add(new MenuEntity("job/farmer", "工农迪奥斯", 1, null));
            job.getChildren().add(new MenuEntity("job/sale", "销售迪奥斯", 2, null));
            cacheMenuList.add(job);

            MenuEntity function = new MenuEntity("function", "功能区", 2, new ArrayList<MenuEntity>());
            function.getChildren().add(new MenuEntity("function/domain", "域名查询", 0, null));
            function.getChildren().add(new MenuEntity("function/ip", "IP查询", 1, null));
            cacheMenuList.add(function);

            MenuEntity joke = new MenuEntity("joke", "段子区", 3, new ArrayList<MenuEntity>());
            cacheMenuList.add(joke);
            MenuEntity news = new MenuEntity("news", "新闻区", 4, new ArrayList<MenuEntity>());
            cacheMenuList.add(news);
            MenuEntity game = new MenuEntity("game", "游戏区", 5, new ArrayList<MenuEntity>());
            cacheMenuList.add(game);
            MenuEntity life = new MenuEntity("life", "生活区", 6, new ArrayList<MenuEntity>());
            cacheMenuList.add(life);
        }
    };

    @Override
    public String getLocation(HttpServletRequest request) {
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            initCacheMenuList();
        }
        String requestURI = request.getRequestURI();
        String code = requestURI.replace("/jsp/", "").replace(".jsp", "");
        StringBuilder sb = new StringBuilder();
        for (MenuEntity parent : cacheMenuList) {
            String[] split = code.split("/");
            if (split[0].equals(parent.getCode())) {
                sb.append("<a href=\"/jsp/" + parent.getCode() + ".jsp\">" + parent.getName() + "</a>");
            }
            if (split.length == 2) {
                for (MenuEntity child : parent.getChildren()) {
                    if (code.equals(child.getCode())) {
                        sb.append("--><a href=\"/jsp/" + child.getCode() + ".jsp\">" + child.getName() + "</a>");
                    }
                }
            }

        }
        return sb.toString();
    }

    @Override
    public MenuEntity getCurrentMenu(HttpServletRequest request) {
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            initCacheMenuList();
        }
        String requestURI = request.getRequestURI();
        String code = requestURI.replace("/jsp/", "").replace(".jsp", "");
        for (MenuEntity MenuEntity : cacheMenuList) {
            if (code.equals(MenuEntity.getCode())) {
                return MenuEntity;
            }
        }
        return null;
    }

    @Override
    public MenuEntity getParentMenu(HttpServletRequest request) {
        if (cacheMenuList == null || cacheMenuList.isEmpty()) {
            initCacheMenuList();
        }
        String requestURI = request.getRequestURI();
        String code = requestURI.replace("/jsp/", "").replace(".jsp", "");
        for (MenuEntity MenuEntity : cacheMenuList) {
            if (code.split("/")[0].equals(MenuEntity.getCode())) {
                return MenuEntity;
            }
        }
        return null;
    }
}
