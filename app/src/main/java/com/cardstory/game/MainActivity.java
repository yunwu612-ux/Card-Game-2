package com.cardstory.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.content.SharedPreferences;
import java.util.*;

public class MainActivity extends Activity {
    private final int BG=Color.rgb(9,13,27), PANEL=Color.rgb(19,27,48), CYAN=Color.rgb(93,228,255), WHITE=Color.rgb(238,245,255), MUTED=Color.rgb(151,169,198), GOLD=Color.rgb(255,203,92);
    private LinearLayout root, body, header, nav;
    private SharedPreferences save;
    private int coins, shards, shopLevel, totalSales, activeTab=0, collectionFilter=0;
    private final HashMap<Integer,Integer> owned=new HashMap<>(), levels=new HashMap<>();
    private final ArrayList<CardData> deck=new ArrayList<>();
    private String notice="欢迎来到星际机甲商店！";
    private static class CardData { int id; String name, rarity, role, art; int power, income; int color; CardData(int i,String n,String r,String ro,String a,int p,int inc,int c){id=i;name=n;rarity=r;role=ro;art=a;power=p;income=inc;color=c;} }

    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);save=getSharedPreferences("cardstory_save",MODE_PRIVATE); buildDeck(); loadSave(); render();}
    private void buildDeck(){
        String[] prefixes={"苍穹","赤焰","深海","雷霆","幽影","星尘","白虎机","天穹","零式","极光","破晓","黑曜","银翼","量子","风暴","龙骑","圣盾","幻影","裂星","超新星"};
        String[] suffixes={"先锋","守卫","狙击手","重装兵","巡游者","工程师"};
        String[] rarities={"普通","普通","普通","稀有","稀有","史诗","传说","神话"};
        String[] arts={"🤖","🦾","🚀","🛡️","⚡","👾","🛰️","🔱","💠","🔥","🌌","⚙️"};
        int[] colors={0xff43c6e8,0xff6c83ff,0xffc06bff,0xffff9364,0xfff4c65e,0xff55d6a5};
        for(int i=0;i<120;i++){
            String name=prefixes[i%prefixes.length]+suffixes[(i/3+i%6)%suffixes.length]+"·"+String.format(java.util.Locale.ROOT,"%03d",i+1);
            String rarity=i<48?"普通":i<82?"稀有":i<104?"史诗":i<116?"传说":"神话";
            int power=35+(i*17)%190+(rarity.equals("神话")?100:rarity.equals("传说")?55:rarity.equals("史诗")?25:0);
            int income=4+(i*3)%18+(rarity.equals("神话")?16:rarity.equals("传说")?8:0);
            deck.add(new CardData(i,name,rarity,new String[]{"战斗","经营","研发","防御"}[i%4],arts[(i*5+i/4)%arts.length],power,income,colors[i%colors.length]));
        }
    }
    private void loadSave(){coins=save.getInt("coins",1200);shards=save.getInt("shards",8);shopLevel=save.getInt("shop",1);totalSales=save.getInt("sales",0);for(CardData c:deck){int n=save.getInt("owned_"+c.id,0);if(n>0)owned.put(c.id,n);int l=save.getInt("level_"+c.id,1);levels.put(c.id,l);} if(owned.isEmpty()){owned.put(0,1);owned.put(3,1);}}
    private void persist(){SharedPreferences.Editor e=save.edit().putInt("coins",coins).putInt("shards",shards).putInt("shop",shopLevel).putInt("sales",totalSales);for(CardData c:deck){e.putInt("owned_"+c.id,owned.containsKey(c.id)?owned.get(c.id):0);e.putInt("level_"+c.id,levels.containsKey(c.id)?levels.get(c.id):1);}e.apply();}
    private GradientDrawable shape(int color,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(16));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private void pad(View v,int a,int b,int c,int d){v.setPadding(dp(a),dp(b),dp(c),dp(d));}
    private TextView button(String label,Runnable action){TextView t=text(label,14,WHITE,true);t.setGravity(Gravity.CENTER);pad(t,14,12,14,12);t.setBackground(shape(0xff253957,CYAN));t.setOnClickListener(v->action.run());return t;}
    private void render(){
        root=column();root.setBackgroundColor(BG);setContentView(root);
        header=column();pad(header,18,12,18,10);header.setBackgroundColor(0xff0d1428);
        LinearLayout top=row();TextView logo=text("✦ 卡牌物语",22,WHITE,true);top.addView(logo,new LinearLayout.LayoutParams(0,-2,1));top.addView(text("机甲商会 · V2.0",11,CYAN,true));header.addView(top);
        LinearLayout resources=row();resources.setPadding(0,dp(12),0,0);resources.addView(resource("◈  "+coins+" 金币"),new LinearLayout.LayoutParams(0,-2,1));resources.addView(resource("◆  "+shards+" 星核"),new LinearLayout.LayoutParams(0,-2,1));header.addView(resources);root.addView(header);
        ScrollView sc=new ScrollView(this);body=column();pad(body,16,16,16,22);sc.setFillViewport(true);sc.addView(body);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        nav=row();nav.setBackgroundColor(0xff0d1428);String[] tabs={"🏪 经营","🎴 抽卡","📚 图鉴"};for(int i=0;i<3;i++){final int ix=i;TextView t=text(tabs[i],13,i==activeTab?CYAN:MUTED,true);t.setGravity(Gravity.CENTER);pad(t,2,16,2,16);t.setBackground(i==activeTab?shape(0xff1a2d48,CYAN):shape(0xff0d1428,0));t.setOnClickListener(v->{activeTab=ix;render();});nav.addView(t,new LinearLayout.LayoutParams(0,-2,1));}root.addView(nav);
        if(activeTab==0)showShop();else if(activeTab==1)showGacha();else showCollection();
    }
    private TextView resource(String s){TextView t=text(s,13,GOLD,true);t.setGravity(Gravity.CENTER);pad(t,8,8,8,8);t.setBackground(shape(0xff18233a,0));return t;}
    private void section(String title,String sub){body.addView(text(title,21,WHITE,true));TextView s=text(sub,12,MUTED,false);s.setPadding(0,dp(5),0,dp(12));body.addView(s);}
    private void showShop(){
        section("星际机甲商店","招募机甲员工、接待顾客，把小店经营成星际连锁商会。");
        LinearLayout hero=column();pad(hero,18,18,18,18);hero.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff183c62,0xff302553,0xff171d38}));((GradientDrawable)hero.getBackground()).setCornerRadius(dp(20));
        hero.addView(text("ORBITAL MECHA MARKET",10,CYAN,true));hero.addView(text("🚀  第 "+shopLevel+" 号旗舰店",24,WHITE,true));hero.addView(text("店铺等级 Lv."+shopLevel+"   ·   今日成交 "+totalSales+" 单",13,0xffd4e3ff,false));hero.addView(space(12));
        TextView sell=button("接待顾客  +"+(35+shopLevel*12+teamIncome())+" 金币",()->{int gain=35+shopLevel*12+teamIncome();coins+=gain;totalSales++;notice="成交成功！获得 "+gain+" 金币。";persist();render();});hero.addView(sell);body.addView(hero);
        body.addView(space(14));body.addView(text("经营管理",18,WHITE,true));body.addView(text("员工卡牌会提供额外收益，升级店铺可提升每笔订单收入。",12,MUTED,false));body.addView(space(8));
        LinearLayout stats=row();stats.addView(stat("员工数量",ownedCount()+" 张","已招募卡牌"),new LinearLayout.LayoutParams(0,-2,1));stats.addView(stat("团队加成","+"+teamIncome(),"每单额外金币"),new LinearLayout.LayoutParams(0,-2,1));stats.addView(stat("店铺等级","Lv."+shopLevel,"持续成长"),new LinearLayout.LayoutParams(0,-2,1));body.addView(stats);
        body.addView(space(12));int cost=250+shopLevel*180;body.addView(button("升级商店  ·  "+cost+" 金币",()->{if(coins>=cost){coins-=cost;shopLevel++;notice="店铺升级成功！订单收益提高。";}else notice="金币不足，先接待顾客赚取金币吧。";persist();render();}));
        body.addView(space(8));body.addView(button("每日补给 · 领取 300 金币 + 2 星核",()->claimDaily()));
        body.addView(space(12));body.addView(text("经营任务",18,WHITE,true));
        body.addView(text("接待顾客："+Math.min(totalSales,10)+" / 10",12,MUTED,false));
        ProgressBar salesBar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);salesBar.setMax(10);salesBar.setProgress(Math.min(totalSales,10));body.addView(salesBar,new LinearLayout.LayoutParams(-1,dp(8)));
        body.addView(text("收集机甲："+Math.min(uniqueOwned(),20)+" / 20",12,MUTED,false));
        body.addView(space(8));body.addView(text("商会公告",16,CYAN,true));TextView n=text("✦  "+notice,13,WHITE,false);pad(n,14,14,14,14);n.setBackground(shape(PANEL,0));body.addView(n);
        body.addView(space(14));body.addView(text("推荐员工",18,WHITE,true));int shown=0;for(CardData c:deck){if(owned.getOrDefault(c.id,0)>0){body.addView(cardView(c,true));if(++shown==3)break;}}
        body.addView(space(12));body.addView(button("前往招募机甲员工 →",()->{activeTab=1;render();}));
    }
    private void claimDaily(){String today=new java.text.SimpleDateFormat("yyyyMMdd",Locale.ROOT).format(new java.util.Date());String claimed=save.getString("daily_claimed"," ");if(today.equals(claimed)){notice="今日补给已经领取，明天再来！";}else{coins+=300;shards+=2;save.edit().putString("daily_claimed",today).apply();notice="每日补给到账：300 金币、2 星核！";}persist();render();}
    private View space(int h){View v=new View(this);v.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return v;}
    private View stat(String title,String value,String foot){LinearLayout l=column();pad(l,10,12,6,12);l.setBackground(shape(PANEL,0));l.addView(text(title,11,MUTED,true));l.addView(text(value,18,CYAN,true));l.addView(text(foot,10,MUTED,false));return l;}
    private int ownedCount(){int n=0;for(int x:owned.values())n+=x;return n;}
    private int teamIncome(){int n=0;for(Integer id:owned.keySet()){CardData c=deck.get(id);n+=c.income*owned.get(id)*levels.getOrDefault(id,1)/2;}return n;}
    private void showGacha(){section("机甲招募中心","消耗金币进行标准招募，或使用星核进行高级招募。重复卡牌会转化为星核。");
        LinearLayout banner=column();pad(banner,18,18,18,18);banner.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff33285b,0xff163b60}));((GradientDrawable)banner.getBackground()).setCornerRadius(dp(20));banner.addView(text("LIMITED MECHA RECRUIT",10,GOLD,true));banner.addView(text("✦ 星核共振 · 机甲召集",23,WHITE,true));banner.addView(text("120 张机甲卡牌 · 6 种定位 · 5 个稀有等级",12,0xffd9e5ff,false));banner.addView(space(12));banner.addView(button("单次招募  ·  100 金币",()->draw(false)));banner.addView(space(8));banner.addView(button("十连招募  ·  900 金币",()->draw(true)));banner.addView(space(8));banner.addView(button("星核招募  ·  3 星核",()->drawShard()));body.addView(banner);
        body.addView(space(16));body.addView(text("招募记录",18,WHITE,true));body.addView(text(notice,13,MUTED,false));body.addView(space(10));body.addView(text("稀有度概率",15,WHITE,true));String[] rr={"普通","稀有","史诗","传说","神话"};String[] pp={"40%","28%","18%","10%","4%"};for(int i=0;i<rr.length;i++){LinearLayout r=row();r.addView(text(rr[i],13,rarityColor(rr[i]),true),new LinearLayout.LayoutParams(0,-2,1));r.addView(text(pp[i],13,WHITE,true));pad(r,12,10,12,10);r.setBackground(shape(PANEL,0));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(5);body.addView(r,lp);}
    }
    private void draw(boolean ten){int cost=ten?900:100;if(coins<cost){notice="金币不足！先回商店接待顾客。";render();return;}coins-=cost;StringBuilder out=new StringBuilder(ten?"十连招募结果：\n":"招募成功：\n");Random random=new Random();int count=ten?10:1;for(int i=0;i<count;i++){CardData c=pick(random.nextInt(100));addCard(c);out.append(c.rarity).append(" · ").append(c.name);if(i<count-1)out.append("\n");}notice=out.toString();persist();render();}
    private void drawShard(){if(shards<3){notice="星核不足！重复卡牌可转化为星核。";render();return;}shards-=3;Random r=new Random();int roll=r.nextInt(100);CardData c=roll<10?pick(99):roll<35?pick(85):roll<70?pick(65):pick(50);addCard(c);notice="星核共振获得："+c.rarity+" · "+c.name;persist();render();}
    private CardData pick(int roll){int id; if(roll<40)id=new Random().nextInt(48);else if(roll<68)id=48+new Random().nextInt(34);else if(roll<86)id=82+new Random().nextInt(22);else if(roll<96)id=104+new Random().nextInt(12);else id=116+new Random().nextInt(4);return deck.get(id);}
    private void addCard(CardData c){if(owned.containsKey(c.id)){shards+=1+(c.rarity.equals("神话")?4:c.rarity.equals("传说")?2:0);notice="获得重复卡牌，转化为星核。";}else{owned.put(c.id,1);levels.put(c.id,1);}}
    private void showCollection(){section("机甲图鉴","收集并培养你的机甲员工。已收集 "+uniqueOwned()+" / "+deck.size()+" 种卡牌。");
        LinearLayout counts=row();counts.addView(stat("已收集",uniqueOwned()+" / 120","完整图鉴"),new LinearLayout.LayoutParams(0,-2,1));counts.addView(stat("重复持有",duplicateCount()+" 张","可持续招募"),new LinearLayout.LayoutParams(0,-2,1));body.addView(counts);body.addView(space(12));
        LinearLayout filters=row();String[] names={"全部","已拥有","未收集"};for(int i=0;i<3;i++){final int f=i;TextView b=button(names[i],()->{collectionFilter=f;render();});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(2),0,dp(2),0);filters.addView(b,lp);}body.addView(filters);body.addView(space(10));renderCards(collectionFilter);
    }
    private void renderCards(int f){
        LinearLayout gridRow=null; int col=0;
        for(CardData c:deck){
            boolean has=owned.getOrDefault(c.id,0)>0;
            if((f==1&&!has)||(f==2&&has)) continue;
            if(col==0){gridRow=row();gridRow.setGravity(Gravity.TOP);body.addView(gridRow,new LinearLayout.LayoutParams(-1,-2));}
            View v=cardView(c,has); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1f);lp.setMargins(dp(3),dp(3),dp(3),dp(8));gridRow.addView(v,lp);
            col++; if(col==2)col=0;
        }
        if(col==1){View filler=new View(this);gridRow.addView(filler,new LinearLayout.LayoutParams(0,dp(1),1f));}
    }
    private int uniqueOwned(){return owned.size();}private int duplicateCount(){int n=0;for(int x:owned.values())n+=Math.max(0,x-1);return n;}
    private View cardView(CardData c,boolean has){
        LinearLayout card=column();pad(card,8,8,8,10);card.setBackground(shape(0xff141e35,has?rarityColor(c.rarity):0xff29334a));
        int imageId=getResources().getIdentifier(String.format(Locale.ROOT,"card_%03d",c.id+1),"drawable",getPackageName());
        ImageView art=new ImageView(this);art.setImageResource(imageId);art.setScaleType(ImageView.ScaleType.CENTER_CROP);art.setBackground(shape(c.color,rarityColor(c.rarity)));
        art.setClipToOutline(true);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(166));card.addView(art,ap);
        if(!has){art.setColorFilter(0x99aab4c8,android.graphics.PorterDuff.Mode.MULTIPLY);}
        TextView nm=text(c.name,13,has?WHITE:MUTED,true);nm.setMaxLines(1);nm.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams nlp=new LinearLayout.LayoutParams(-1,-2);nlp.topMargin=dp(7);card.addView(nm,nlp);
        card.addView(text(c.rarity+" · "+c.role,10,rarityColor(c.rarity),true));
        card.addView(text(has?"战力 "+(c.power*levels.getOrDefault(c.id,1)):"尚未收集",10,MUTED,false));
        card.addView(text("经营 +"+c.income+" / 单",10,0xffa9c7e9,false));
        if(has){int level=levels.getOrDefault(c.id,1);TextView up=button("Lv."+level+" · 升级 "+(80*level),()->upgrade(c));up.setTextSize(10);LinearLayout.LayoutParams ul=new LinearLayout.LayoutParams(-1,-2);ul.topMargin=dp(6);card.addView(up,ul);}
        else {TextView locked=text("招募解锁详情",10,MUTED,false);locked.setGravity(Gravity.CENTER);card.addView(locked);}
        card.setOnClickListener(v->showCardDetail(c,has));
        return card;
    }
    private void showCardDetail(CardData c,boolean has){
        LinearLayout panel=column();pad(panel,18,10,18,10);panel.setBackgroundColor(0xff101a30);
        int imageId=getResources().getIdentifier(String.format(Locale.ROOT,"card_%03d",c.id+1),"drawable",getPackageName());
        ImageView art=new ImageView(this);art.setImageResource(imageId);art.setScaleType(ImageView.ScaleType.CENTER_CROP);panel.addView(art,new LinearLayout.LayoutParams(-1,dp(260)));
        panel.addView(space(10));panel.addView(text(c.name,21,WHITE,true));panel.addView(text(c.rarity+" · "+c.role,13,rarityColor(c.rarity),true));panel.addView(text("基础战力："+c.power+"    经营收益：+"+c.income+" 金币/单",13,MUTED,false));
        panel.addView(text(has?"当前等级：Lv."+levels.getOrDefault(c.id,1)+"    持有数量："+owned.getOrDefault(c.id,0):"尚未收集 · 前往招募中心获取",12,CYAN,true));
        new android.app.AlertDialog.Builder(this).setView(panel).setPositiveButton(has?"去升级":"知道了",(d,w)->{if(has)upgrade(c);}).setNegativeButton("关闭",null).show();
    }
    private void upgrade(CardData c){int lv=levels.getOrDefault(c.id,1);int cost=80*lv;if(coins<cost){notice="金币不足，无法升级该机甲。";}else{coins-=cost;levels.put(c.id,lv+1);notice=c.name+" 升级成功！当前等级 Lv."+(lv+1)+"。";}persist();render();}
    private int rarityColor(String r){switch(r){case "稀有":return 0xff58b9ff;case "史诗":return 0xffc17aff;case "传说":return 0xffffb85c;case "神话":return 0xffff647e;default:return 0xffa6b5cd;}}
}
