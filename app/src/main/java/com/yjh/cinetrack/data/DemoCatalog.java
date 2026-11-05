package com.yjh.cinetrack.data;

import com.yjh.cinetrack.R;

import java.util.ArrayList;
import java.util.List;

public final class DemoCatalog {
    private static List<DemoMovie> trending;
    private static List<DemoMovie> upcoming;

    private DemoCatalog() {}

    public static List<DemoMovie> trending() {
        if (trending == null) {
            trending = new ArrayList<>();
            trending.add(new DemoMovie(693134, "沙丘 2", "Dune: Part Two",
                    "保罗·厄崔迪与契妮和弗雷曼人并肩作战，踏上一场复仇之路。当他在挚爱与已知宇宙的命运之间做出选择时，战争即将席卷厄拉科斯。",
                    "2024-03-08", 8.2, R.drawable.poster_dune, R.drawable.backdrop_dune, "科幻", 166));
            trending.add(new DemoMovie(872585, "奥本海默", "Oppenheimer",
                    "一位物理学家被推上历史的中心。他创造了改变世界的力量，也从此背负无法卸下的重量。",
                    "2023-08-30", 8.6, R.drawable.poster_oppenheimer, 0, "剧情", 180));
            trending.add(new DemoMovie(414906, "新蝙蝠侠", "The Batman",
                    "哥谭市连环命案频发，年轻的布鲁斯·韦恩循着谜语人的线索，逐渐揭开这座城市更深的腐败与真相。",
                    "2022-03-18", 7.8, R.drawable.poster_batman, 0, "犯罪", 176));
            trending.add(new DemoMovie(157336, "星际穿越", "Interstellar",
                    "地球逐渐失去生机。一群探险家穿越虫洞，为人类寻找新的家园，也在时间的另一端重新理解爱与重逢。",
                    "2014-11-12", 8.4, R.drawable.poster_interstellar, 0, "科幻", 169));
        }
        return trending;
    }

    public static List<DemoMovie> upcoming() {
        if (upcoming == null) {
            upcoming = new ArrayList<>();
            upcoming.add(new DemoMovie(900001, "星际回响", "Stellar Echo",
                    "一段来自宇宙深处的回声，让一支失联多年的深空小队重新出现在雷达上。可当他们返航时，时间已经过去了四十年。",
                    "2026-12-18", 0, R.drawable.poster_stellar, 0, "科幻", 128));
            upcoming.add(new DemoMovie(900002, "霓虹追缉", "Neon Chase",
                    "在永夜之城的霓虹雨里，一名快递员意外拿到一枚记录着城市真相的芯片，整座城市都开始追捕她。",
                    "2026-12-24", 0, R.drawable.poster_neon, 0, "动作", 116));
            upcoming.add(new DemoMovie(900003, "最后一班列车", "The Last Train",
                    "末班列车驶入一条地图上不存在的隧道。车上的七名乘客发现，每一站都对应着他们曾经错过的人生。",
                    "2027-01-08", 0, R.drawable.poster_last_train, 0, "悬疑", 109));
        }
        return upcoming;
    }
}
