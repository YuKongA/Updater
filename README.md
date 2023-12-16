![Updater](https://socialify.git.ci/YuKongA/Updater/image?description=1&descriptionEditable=%E4%B8%80%E4%B8%AA%E7%AE%80%E5%8D%95%E7%9A%84%20HyperOS%2FMIUI%20%E6%9B%B4%E6%96%B0%E9%93%BE%E6%8E%A5%E8%8E%B7%E5%8F%96%E8%BD%AF%E4%BB%B6&language=1&name=1&owner=1&theme=Auto)

## Notes:

无论是否登录, 都可以查看服务器上已存在版本的基础信息

通过 v1 接口 (未登录小米账号状态) 可获取正式版下载链接

使用 v2 接口 (登陆拥有权限的小米账号) 可获取开发版下载链接


## Instructions for use:
正式版后缀: 

```
S/T/U (安卓版本)
NC/NB/MA/.. (机型内部代号)
CN/TW/MI/IN/KR/JP/TR/RU/.. (地区版本)
XM/DM/CC/.. (运营商版本)
```

示例: 
<details>

以 <b>小米 14</b>, 版本号 <b>OS1.0.26.0.UNCCNXM</b> 为例, 其中: <br />
U = Android14(U), NC = 内部代号(C=3, N 世代 3 等机), CN = 中国, XM = 全网通

以 <b>小米 14 Pro</b>, 版本号 <b>OS1.0.27.0.UNBCNXM</b> 为例, 其中: <br />
U = Android14(U), NB = 内部代号(B=2, N 世代 2 等机), CN = 中国, XM = 全网通

以 <b>小米 13 Ultra</b>, 版本号 <b>V14.0.5.0.TMAMIXM</b> 为例, 其中: <br />
T = Android13(T), MA = 内部代号(A=1, M 世代 1 等机), MI = 全球, XM = 全网通

</details>

设备代号后缀: 
```
设备代号: 中国正式版, 中国开发版公测
设备代号_pre: 中国开发版内测
设备代号_global: 全球正式版
设备代号_pre_global: 全球开发版内测
设备代号_国家地区代码_global: 国家/地区专属正式版
设备代号_pre_国家地区代码_global: 国家/地区专属开发版内测
```


已知国家/地区相关后缀：
<details>

"_global", "_eea_global", "_pre_eea_global", "_pre_eea_miui15_global", "_h3g_global", "_eea_hg_global", "_eea_ee_global", "_pre_eea_ee_global", "_eea_or_global", "_eea_tf_global", "_eea_by_global", "_eea_vf_global", "_eea_sf_global", "_eea_ti_global", "_ru_global", "_pre_ru_global", "_in_global", "_pre_in_global", "in_global", "_in_fk_global", "_in_jo_global", "in_in_global", "_id_global", "_pre_id_global", "_tr_global", "_pre_tr_global", "_kr_global", "_kr_gu_global", "_kr_kt_global", "_kr_sk_global", "_jp_global", "_jp_kd_global", "_jp_sb_global", "_jp_rk_global", "_tw_global", "_pre_tw_global", "_global", "_tw_global", "_eea_global", "_ru_global", "_id_global", "_in_global", "in_global", "_in_fk_global", "_kr_global", "in_in_global", "_tr_global", "_jp_global", "_mx_global", "_lm_global", "_th_global", "_pe_global", "_za_global", "_jp_kd_global", "_kr_gu_global", "_kr_kt_global", "_kr_sk_global", "_h3g_global", "_eea_hg_global", "_eea_or_global", "_eea_tf_global", "_eea_by_global", "_eea_vf_global", "_mx_tc_global", "_mx_at_global", "_lm_cr_global", "_cl_en_global", "_cl_global", "_eea_sf_global", "_eea_ti_global", "_th_as_global", "_lm_ms_global", "_pe_ms_global", "_za_vc_global", "_za_mt_global", , "_pre_dpp_global", "_dev_soter_global", "_dc_global", "_test_pre_global", "_pre_miui14_global", "_pre_miui15_global", "_mx_global", "_lm_global", "_th_global", "_pe_global", "_za_global", "_mx_tc_global", "_mx_at_global", "_pre_mx_tc_global", "_pre_mx_at_global", "_lm_cr_global", "_cl_en_global", "_pre_cl_en_global", "_cl_global", "_th_as_global", "_lm_ms_global", "_lm_cr_global", "_pre_lm_cr_global", "_pe_ms_global", "_za_vc_global", "_za_mt_global", "_it_tim_global", "_it_vodafone_global", "_mx_telcel_global", "_es_vodafone_global", "_dck_global", "_gpp_pre_global", "_gt_tg_global", "_gt_global", "_gpp_global", "_qc_global", "_mcaas_global", "_cl_wom_global", "_cl_movistar_global", "_ita_vodafone_global", "_tr_turkcell_global", "_p70_global", "_fr_orange_global", "_wlnd_global"

</details>

其他已知后缀 (2023/12/16): 
<details>

"_pre", "_factory", "_demo", "_ep_yunke", "_soter", "_mfw", "_pre_miui14", "_pre_miui15", "_dev_soter", "_shxc", "_stable_soter", "_hi25", "_by", "_qiy", "_tianyi", "_tq", "_xman", "_yh", "_yfan", "_new", "_3sat", "_beta", "_dev", "_gajw", "_zc360", "zq_360", "_pre_wechat", "_mtk", "_nio", "_bs", "_clb", "_gq", "_hhxa", "_hwl", "_justsafed", "_mdsw", "_ntb", "_xiuyixiu", "_hi2", "_test", "_gpp_pre", "_gpp", "_pre_7475vbl", "_pre_dpp", "_pre_gpp", "_chenfeng", "_dameng", "_ahjw", "_cqjw", "_didi", "_hbjw", "_hnjwxd", "_hnxdjw", "_jili", "_lnjw", "_scjw", "_sjt", "_tianshanjw", "_tangshanjw", "_wanguo", "_whjw", "_ydjw", "_yunnanjw", "_yunke", "_camera", "_port", "_aikesai", "_aochuang", "_hujing", "_kplus", "_ldrh", "_liuniu", "_lsjw", "_qsh", "_tl", "_tly", "_tongzhou", "_tongzhuo", "_wd", "_stable_cmcc", "_stable_cmcc01", "_stable_ct", "_ep_stdee", "_ep_xy", "_ep_kywl", "_ep_cqrcb", "_ep_ec", "_ep_sxht", "_ep_yfan", "_ep_yx", "_ep_stdce", "_ep_xdja", "_ep_litee", "_ep_yy", "_ep_by", "_ep_tq", "_ep_ui", "_ep_wosq", "_ep_xzm", "_ep_dhao", "_ep_qiy", "_ep_tly", "_ep_tlkj", "_ep_zc", "_ep_zdjt", "_ep_zzyglkg", "_ep_zyyglkg", "_ep_zzybp", "_ep_sdlybjcg", "_ep_justsafe", "_ep_justsafed", "_ep_nio", "_ep_txzx", "_ep_dameng", "_ep_yxyun", "_ep_hujing", "_ep_jwm", "_ep_daote", "_ep_jd", "_ep_tpkj", "_ep_tjzf", "_ep_tpybx", "_ep_bds", "_ep_hfw", "_ep_hn", "_ep_jyrj", "_ep_xysw", "_guazi", "_cf", "_gaotu", "_gz", "_hkdw", "_huaxun", "_jds", "_jlxf", "_wanglong", "_wlnd", "_yhai", "_yuxun", "_zkcd", "_cm", "_ct", "_beike", "_yf", "_yskj", "_zyb", "_ep_rb", "_dxo", "_yaohui", "_bcwl", "_czht", "_txzx", "_ep_daotetest", "_jkpd_factory", "_miui13_pre", "_test_pre", "_hmh", "_rrc", "_zy", "_ep_sdlyjcb", "_pre_ep_stdee", "_haozu", "_szkx", "_xmzy", "_yhhl", "_huatian", "_mcaas", "_qkzq", "_qzxx", "_shzl", "_sjyc", "_gzdt", "_lls", "_miui_factory", "_shrq", "_shrx", "_taier", "_tmg", "wuweilab", "_cmcc", "_ajy", "_dp", "_zjzy", "_fs", "_langtuo", "_ep_gy58tc", "_ep_jds", "_ep_yhai", "_zhutai", "_bb2021", "ep_czht", "_ep_mjwxns", "_ep_qdyh", "_research", "_sdlybjcg", "_8475_pre", "_ep_sbgl", "_miui_demo_factory", "_bindsim", "_ent_ct", "_fxtc", "_gzxc", "_dqgx"

</details>

## Credits:

- [Xiaomi-Update-Info](https://github.com/YuKongA/Xiaomi-Update-Info)
- [XiaoMiToolV2](https://github.com/francescotescari/XiaoMiToolV2)
- [Xiaomi-Community-AutoTask](https://github.com/CMDQ8575/Xiaomi-Community-AutoTask)

## More:

关于**设备代号**(_"Code name"_), 请参阅: 
[小米手机型号汇总](https://github.com/KHwang9883/MobileModels/blob/master/brands/xiaomi.md)

关于**国家地区代码**(_"Country codes"_), 请参阅: 
[ISO 3166-2](https://en.wikipedia.org/wiki/ISO_3166-2#Current_codes)
