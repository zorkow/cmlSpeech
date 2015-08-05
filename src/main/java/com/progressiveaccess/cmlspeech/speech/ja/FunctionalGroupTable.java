// Copyright 2015 Volker Sorge
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @file   FunctionalGroupTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Aug  2 12:58:22 2015
 *
 * @brief  Singleton class to translate functional group names to Japanese.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.ja;

import com.progressiveaccess.cmlspeech.speech.AbstractFunctionalGroupTable;

/**
 * Maps functional group names to Japanese whenever possible.
 */
public final class FunctionalGroupTable extends AbstractFunctionalGroupTable {

  private static final long serialVersionUID = 1L;

  /**
   * Japanese functional group table.
   */
  public FunctionalGroupTable() {
    this.put("Alkyl Carbon", "アルキル炭素");
    this.put("Allenic Carbon", "アレン系炭素");
    this.put("Vinylic Carbon", "ビニル炭素");
    this.put("Acetylenic Carbon", "アセチレン炭素");
    this.put("Methyl", "メチル");
    this.put("Methylene", "メチレン");
    this.put("Methine", "メチン");
    this.put("Carbonyl group", "カルボニル基");
    this.put("Carbonyl with Carbon", "炭素とカルボニル");
    this.put("Carbonyl with Nitrogen", "窒素とカルボニル");
    this.put("Carbonyl with Oxygen", "酸素とカルボニル");
    this.put("Acyl Halide", "ハロゲン化アシル");
    this.put("Aldehyde", "アルデヒド");
    this.put("Anhydride", "無水物");
    this.put("Amide", "アミド");
    this.put("Amidinium", "アミジニウム");
    this.put("Carbamate", "カルバミン酸塩");
    this.put("Carbamic ester", "カルバミン酸エステル");
    this.put("Carbamic acid", "カルバミン酸");
    this.put("Carboxylate Ion", "カルボン酸イオン");
    this.put("Carbonic acid", "炭酸");
    this.put("Carbonic ester", "炭酸エステル");
    this.put("Carbonic diester", "炭酸ジエステル");
    this.put("Carboxylic acid", "カルボン酸");
    this.put("Carboxylic acid or conjugate base", "カルボン酸または共役塩基");
    this.put("Cyanamide", "シアナミド");
    this.put("Ester", "エスター");
    this.put("Ketone", "ケトン");
    this.put("Ether", "エーテル");
    this.put("Amine", "アミン");
    this.put("Enamine", "エナミン");
    this.put("Primary amine", "1級アミン");
    this.put("Two primary or secondary amines",
             "二つの第一級または第二級アミン");
    this.put("Enamine", "エナミン");
    this.put("Generic amino acid low specificity",
             "一般的なアミノ酸特異性が低いです");
    this.put("Dipeptide group. generic amino acid low specificity",
             "ジペプチド基。 一般的なアミノ酸特異性が低いです");
    this.put("Amino Acid", "アミノ酸");
    this.put("Alanine", "アラニン");
    this.put("Arginine", "アルギニン");
    this.put("Aspargine", "アスパラギン");
    this.put("Aspartate", "アスパラギン酸塩");
    this.put("Cysteine", "システイン");
    this.put("Glutamate", "グルタミン酸塩");
    this.put("Glycine", "グリシン");
    this.put("Histidine", "ヒスチジン");
    this.put("Isoleucine", "イソロイシン");
    this.put("Leucine", "ロイシン");
    this.put("Lysine", "リジン");
    this.put("Methionine", "メチオニン");
    this.put("Phenylalanine", "フェニルアラニン");
    this.put("Proline", "プロライン");
    this.put("Serine", "セリン");
    this.put("Thioamide", "チオアミド");
    this.put("Threonine", "スレオニン");
    this.put("Tryptophan", "トリプトファン");
    this.put("Tyrosine", "チロシン");
    this.put("Valine", "バリン");
    this.put("Azide group", "アジド基");
    this.put("Azide ion", "アジ化物イオン");
    this.put("Nitrogen", "窒素");
    this.put("Azo Nitrogen. Low specificity", "アゾ窒素。 低特異性");
    this.put("Azo Nitrogen.diazene", "アゾNitrogen.diazene");
    this.put("Azoxy Nitrogen", "アゾキシ窒素");
    this.put("Diazo Nitrogen", "ジアゾ窒素");
    this.put("Azole", "アゾール");
    this.put("Hydrazine H2NNH2", "ヒドラジンH2NNH2");
    this.put("Hydrazone C=NNH2", "ヒドラゾンC = NNH2");
    this.put("Substituted imine", "置換イミン");
    this.put("Substituted or un-substituted imine", "置換または非置換イミン");
    this.put("Iminium", "イミニウム");
    this.put("Unsubstituted dicarboximide", "非置換カルボキシイミド");
    this.put("Substituted dicarboximide", "置換ジカルボキシイミド");
    this.put("Dicarboxdiimide", "Dicarboxdiimide");
    this.put("Nitrate group", "硝酸グループ");
    this.put("Nitrate Anion", "硝酸アニオン");
    this.put("Nitrile", "ニトリル");
    this.put("Isonitrile", "イソニトリル");
    this.put("Nitro group", "ニトロ基");
    this.put("Two Nitro groups", "二つのニトロ基");
    this.put("Nitroso-group", "ニトロソ基");
    this.put("N-Oxide", "N-オキシド");
    this.put("Hydroxyl", "水酸基");
    this.put("Hydroxyl in Alcohol", "アルコール中の水酸基");
    this.put("Hydroxyl in Carboxylic Acid", "カルボン酸中の水酸基");
    this.put("Hydroxyl in H-O-P-", "ホッパー中の水酸基");
    this.put("Enol", "エノール");
    this.put("Phenol", "フェノール");
    this.put("Enol or Phenol", "エノールまたはフェノール");
    this.put("Hydroxyl Acidic", "ヒドロキシル酸性");
    this.put("Peroxide groups", "過酸化物グループ");
    this.put("Carbo-Thiocarboxylate", "カルボチオカルボキシレート");
    this.put("Carbo-Thioester", "カルボチオエステル");
    this.put("Thio analog of carbonyl", "カルボニルのチオアナログ");
    this.put("Thiol Sulfide or Disulfide Sulfur",
             "チオール、硫化又はジスルフィド硫黄");
    this.put("Thiol", "チオール");
    this.put("Sulfur with at-least one hydrogen",
             "アット少なくとも一つの水素と硫黄");
    this.put("Thioamide", "チオアミド");
    this.put("Sulfide", "硫化物");
    this.put("Mono-sulfide", "モノスルフィド");
    this.put("Di-sulfide", "ジ硫化物");
    this.put("Two Sulfides", "二硫化物");
    this.put("Sulfinate", "スルフィン酸塩");
    this.put("Sulfinic Acid", "スルフィン酸");
    this.put("Sulfone. Low specificity", "スルホン。 低特異性");
    this.put("Sulfone. High specificity", "スルホン。 高特異性");
    this.put("Sulfonic acid.  High specificity", "スルホン酸。 Â高い特異性");
    this.put("Sulfonate", "スルホン酸塩");
    this.put("Sulfonamide", "スルホンアミド");
    this.put("Carbo-azosulfone", "炭azosulfone");
    this.put("Sulfonamide", "スルホンアミド");
    this.put("Sulfoxide Low specificity", "スルホキシド低特異性");
    this.put("Sulfoxide High specificity", "スルホキシド高い特異性");
    this.put("Sulfate", "硫酸塩");
    this.put("Sulfuric acid ester (sulfate ester)  Low specificity",
             "硫酸エステル（硫酸エステル）Â低特異性");
    this.put("Sulfuric Acid Diester", "硫酸ジエステル");
    this.put("Sulfamate", "スルファミン酸塩");
    this.put("Sulfamic Acid", "スルファミン酸");
    this.put("Sulfenic acid", "スルフェン酸");
    this.put("Sulfenate", "Sulfenate");
    this.put("Halogen", "ハロゲン");
    this.put("Three_halides groups", "グループThree_halides");
    this.put("Acyl Halide", "ハロゲン化アシル");
    this.put("Alkene", "アルケン");
    this.put("Alkyne", "アルキン");
    this.put("Allene", "アレン");
    this.put("Alkylchloride", "Alkylchloride");
    this.put("Alkylfluoride", "Alkylfluoride");
    this.put("Alkylbromide", "臭化アルキル");
    this.put("Alkyliodide", "Alkyliodide");
    this.put("Alcohol", "アルコール");
    this.put("Primary alcohol", "第一級アルコール");
    this.put("Secondary alcohol", "第2級アルコール");
    this.put("Tertiary alcohol", "第3級アルコール");
    this.put("Dialkylether", "ジアルキル");
    this.put("Dialkylthioether", "Dialkylthioether");
    this.put("Alkylarylether", "アルキルアリール");
    this.put("Diarylether", "ジアリール");
    this.put("Alkylarylthioether", "Alkylarylthioether");
    this.put("Diarylthioether", "Diarylthioether");
    this.put("Oxonium", "オキソニウム");
    this.put("Amine", "アミン");
    this.put("Primary aliph amine", "プライマリアリフアミン");
    this.put("Secondary aliph amine", "セカンダリアリフアミン");
    this.put("Tertiary aliph amine", "第三級アミンアリフ");
    this.put("Quaternary aliph ammonium", "第四級アンモニウムアリフ");
    this.put("Primary arom amine", "主な芳香族アミン");
    this.put("Secondary arom amine", "セカンダリ芳香族アミン");
    this.put("Tertiary arom amine", "第三級芳香族アミン");
    this.put("Quaternary arom ammonium", "第四級芳香族アンモニウム");
    this.put("Secondary mixed amine", "二次混合アミン");
    this.put("Tertiary mixed amine", "第三級混合アミン");
    this.put("Quaternary mixed ammonium", "第四級アンモニウム混合");
    this.put("Ammonium", "アンモニウム");
    this.put("Alkylthiol", "アルキルチオール");
    this.put("Dialkylthioether", "Dialkylthioether");
    this.put("Alkylarylthioether", "Alkylarylthioether");
    this.put("Disulfide", "ジスルフィド");
    this.put("1,2-Aminoalcohol", "1,2-アミノアルコール");
    this.put("1,2-Diol", "1,2-ジオール");
    this.put("1,1-Diol", "1,1-ジオール");
    this.put("Hydroperoxide", "ヒドロペルオキシド");
    this.put("Peroxo", "ペルオキソ");
    this.put("Organolithium compounds", "有機リチウム化合物");
    this.put("Organomagnesium compounds", "有機マグネシウム化合物");
    this.put("Organometallic compounds", "有機金属化合物");
    this.put("Aldehyde", "アルデヒド");
    this.put("Ketone", "ケトン");
    this.put("Thioaldehyde", "チオアルデヒド");
    this.put("Thioketone", "チオケトン");
    this.put("Imine", "イミン");
    this.put("Immonium", "インモニウム");
    this.put("Oxime", "オキシム");
    this.put("Oximether", "Oximether");
    this.put("Acetal", "アセタール");
    this.put("Hemiacetal", "ヘミアセタール");
    this.put("Aminal", "アミナール");
    this.put("Hemiaminal", "ヘミアミナール");
    this.put("Thioacetal", "チオアセタール");
    this.put("Thiohemiacetal", "Thiohemiacetal");
    this.put("Halogen acetal like", "ハロゲンアセタールのような");
    this.put("Acetal like", "アセタールのような");
    this.put("Halogenmethylen ester and similar",
             "Halogenmethylenエステルと同様の");
    this.put("NOS methylen ester and similar", "NOSメチレンエステルと同様の");
    this.put("Hetero methylen ester and similar",
             "ヘテロメチレンエステルと同様の");
    this.put("Cyanhydrine", "Cyanhydrine");
    this.put("Chloroalkene", "Chloroalkene");
    this.put("Fluoroalkene", "フルオロアルケン");
    this.put("Bromoalkene", "ブロモアルケン");
    this.put("Iodoalkene", "Iodoalkene");
    this.put("Enol", "エノール");
    this.put("Endiol", "エンジオール");
    this.put("Enolether", "エノールエーテル");
    this.put("Enolester", "Enolester");
    this.put("Enamine", "エナミン");
    this.put("Thioenol", "チオエノール");
    this.put("Thioenolether", "Thioenolether");
    this.put("Acylchloride", "塩化アシル");
    this.put("Acylfluoride", "フッ化アシル");
    this.put("Acylbromide", "Acylbromide");
    this.put("Acyliodide", "Acyliodide");
    this.put("Acylhalide", "アシルハライド");
    this.put("Carboxylic acid", "カルボン酸");
    this.put("Carboxylic ester", "カルボン酸エステル");
    this.put("Lactone", "ラクトン");
    this.put("Carboxylic anhydride", "カルボン酸無水物");
    this.put("Carboxylic acid derivative", "カルボン酸誘導体");
    this.put("Carbothioic acid", "カルボチオ酸");
    this.put("Carbothioic S ester", "カルボチオSエステル");
    this.put("Carbothioic S lactone", "カルボチオSラクトン");
    this.put("Carbothioic O ester", "カルボチオOエステル");
    this.put("Carbothioic O lactone", "カルボチオOラクトン");
    this.put("Carbothioic halide", "カルボチオハロゲン化物");
    this.put("Carbodithioic acid", "Carbodithioic酸");
    this.put("Carbodithioic ester", "Carbodithioicエステル");
    this.put("Carbodithiolactone", "Carbodithiolactone");
    this.put("Amide", "アミド");
    this.put("Primary amide", "第一アミド");
    this.put("Secondary amide", "二級アミド");
    this.put("Tertiary amide", "第3級アミド");
    this.put("Lactam", "ラクタム");
    this.put("Alkyl imide", "アルキルイミド");
    this.put("N hetero imide", "Nヘテロイミド");
    this.put("Imide acidic", "酸性イミド");
    this.put("Thioamide", "チオアミド");
    this.put("Thiolactam", "チオラクタム");
    this.put("Oximester", "Oximester");
    this.put("Amidine", "アミジン");
    this.put("Hydroxamic acid", "ヒドロキサム酸");
    this.put("Hydroxamic acid ester", "ヒドロキサム酸エステル");
    this.put("Imidoacid", "Imidoacid");
    this.put("Imidoacid cyclic", "Imidoacid巡回");
    this.put("Imidoester", "イミドエステル");
    this.put("Imidolactone", "Imidolactone");
    this.put("Imidothioacid", "Imidothioacid");
    this.put("Imidothioacid cyclic", "Imidothioacid巡回");
    this.put("Imidothioester", "Imidothioester");
    this.put("Imidothiolactone", "Imidothiolactone");
    this.put("Amidine", "アミジン");
    this.put("Imidolactam", "Imidolactam");
    this.put("Imidoylhalide", "Imidoylhalide");
    this.put("Imidoylhalide cyclic", "Imidoylhalide巡回");
    this.put("Amidrazone", "アミドラゾン");
    this.put("Alpha aminoacid", "アルファアミノ酸");
    this.put("Alpha hydroxyacid", "アルファヒドロキシ酸");
    this.put("Peptide middle", "ペプチドミドル");
    this.put("Peptide C term", "ペプチドのC項");
    this.put("Peptide N term", "ペプチドのN用語");
    this.put("Carboxylic orthoester", "カルボンオルトエステル");
    this.put("Ketene", "ケテン");
    this.put("Ketenacetal", "Ketenacetal");
    this.put("Nitrile", "ニトリル");
    this.put("Isonitrile", "イソニトリル");
    this.put("Vinylogous carbonyl or carboxyl derivative",
             "ビニル性カルボニルまたはカルボキシル誘導体");
    this.put("Vinylogous acid", "ビニル性酸");
    this.put("Vinylogous ester", "ビニル性エステル");
    this.put("Vinylogous amide", "ビニル性アミド");
    this.put("Vinylogous halide", "ビニル性ハライド");
    this.put("Carbonic acid dieester", "炭酸dieester");
    this.put("Carbonic acid esterhalide", "炭酸esterhalide");
    this.put("Carbonic acid monoester", "炭酸モノエステル");
    this.put("Carbonic acid derivatives", "炭酸誘導体");
    this.put("Thiocarbonic acid dieester", "チオ酸dieester");
    this.put("Thiocarbonic acid esterhalide", "チオ酸esterhalide");
    this.put("Thiocarbonic acid monoester", "チオ酸モノエステル");
    this.put("Thiourea", "チオ尿素");
    this.put("Isourea", "イソ尿素");
    this.put("Isothiourea", "イソチオウレア");
    this.put("Guanidine", "グアニジン");
    this.put("Carbaminic acid", "カルバミン酸");
    this.put("Urethan", "ウレタン");
    this.put("Biuret", "ビウレット");
    this.put("Semicarbazide", "セミカルバジド");
    this.put("Carbazide", "カルバジド");
    this.put("Semicarbazone", "セミカルバゾン");
    this.put("Carbazone", "Carbazone");
    this.put("Thiosemicarbazide", "チオセミカルバジド");
    this.put("Thiocarbazide", "チオカルバジド");
    this.put("Thiosemicarbazone", "チオセミカルバゾン");
    this.put("Thiocarbazone", "チオカルバゾン");
    this.put("Isocyanate", "イソシアネート");
    this.put("Cyanate", "シアン酸塩");
    this.put("Isothiocyanate", "イソチオシアネート");
    this.put("Thiocyanate", "チオシアン酸塩");
    this.put("Carbodiimide", "カルボジイミド");
    this.put("Orthocarbonic derivatives", "Orthocarbonicデリバティブ");
    this.put("Phenol", "フェノール");
    this.put("1,2-Diphenol", "1,2-ジフェノール");
    this.put("Arylchloride", "Arylchloride");
    this.put("Arylfluoride", "Arylfluoride");
    this.put("Arylbromide", "臭化アリール");
    this.put("Aryliodide", "Aryliodide");
    this.put("Arylthiol", "アリールチオール");
    this.put("Iminoarene", "Iminoarene");
    this.put("Oxoarene", "Oxoarene");
    this.put("Thioarene", "Thioarene");
    this.put("Hetero N basic H", "ヘテロN基本H");
    this.put("Hetero N basic no H", "ヘテロN基本ノーH");
    this.put("Hetero N nonbasic", "ヘテロN非塩基性");
    this.put("Hetero O", "ヘテロO");
    this.put("Hetero S", "ヘテロS");
    this.put("Heteroaromatic", "ヘテロ芳香族");
    this.put("Nitrite", "亜硝酸塩");
    this.put("Thionitrite", "チオニトリト");
    this.put("Nitrate", "硝酸塩");
    this.put("Nitro", "ナイトロ");
    this.put("Nitroso", "ニトロソ");
    this.put("Azide", "アジド");
    this.put("Acylazide", "アシルアジド");
    this.put("Diazo", "ジアゾ");
    this.put("Diazonium", "ジアゾニウム");
    this.put("Nitrosamine", "ニトロソアミン");
    this.put("Nitrosamide", "ニトロソアミド");
    this.put("N-Oxide", "N-オキシド");
    this.put("Hydrazine", "ヒドラジン");
    this.put("Hydrazone", "ヒドラゾン");
    this.put("Hydroxylamine", "ヒドロキシルアミン");
    this.put("Sulfon", "スルホン");
    this.put("Sulfoxide", "スルホキシド");
    this.put("Sulfonium", "スルホニウム");
    this.put("Sulfuric acid", "硫酸");
    this.put("Sulfuric monoester", "硫酸モノエステル");
    this.put("Sulfuric diester", "硫酸ジエステル");
    this.put("Sulfuric monoamide", "硫酸モノアミド");
    this.put("Sulfuric diamide", "硫酸ジアミド");
    this.put("Sulfuric esteramide", "硫酸エステルアミド");
    this.put("Sulfuric derivative", "硫酸デリバティブ");
    this.put("Sulfonic acid", "スルホン酸");
    this.put("Sulfonamide", "スルホンアミド");
    this.put("Sulfonic ester", "スルホン酸エステル");
    this.put("Sulfonic halide", "スルホン化物");
    this.put("Sulfonic derivative", "スルホンデリバティブ");
    this.put("Sulfinic acid", "スルフィン酸");
    this.put("Sulfinic amide", "スルフィンアミド");
    this.put("Sulfinic ester", "スルフィン酸エステル");
    this.put("Sulfinic halide", "スルフィンハライド");
    this.put("Sulfinic derivative", "スルフィン誘導体");
    this.put("Sulfenic acid", "スルフェン酸");
    this.put("Sulfenic amide", "スルフェンアミド");
    this.put("Sulfenic ester", "スルフェン酸エステル");
    this.put("Sulfenic halide", "スルフェンハロゲン化物");
    this.put("Sulfenic derivative", "スルフェンデリバティブ");
    this.put("Phosphine", "ホスフィン");
    this.put("Phosphine oxide", "ホスフィンオキシド");
    this.put("Phosphonium", "ホスホニウム");
    this.put("Phosphorylen", "Phosphorylen");
    this.put("Phosphonic acid", "ホスホン酸");
    this.put("Phosphonic monoester", "ホスホン酸モノエステル");
    this.put("Phosphonic diester", "ホスホン酸ジエステル");
    this.put("Phosphonic monoamide", "ホスホンモノアミド");
    this.put("Phosphonic diamide", "ホスホンジアミド");
    this.put("Phosphonic esteramide", "ホスホンエステルアミド");
    this.put("Phosphonic acid derivative", "ホスホン酸誘導体");
    this.put("Phosphoric acid", "リン酸");
    this.put("Phosphoric monoester", "リン酸モノエステル");
    this.put("Phosphoric diester", "リン酸ジエステル");
    this.put("Phosphoric triester", "リン酸トリエステル");
    this.put("Phosphoric monoamide", "リンモノアミド");
    this.put("Phosphoric diamide", "リンジアミド");
    this.put("Phosphoric triamide", "リン酸トリアミド");
    this.put("Phosphoric monoestermonoamide", "リンmonoestermonoamide");
    this.put("Phosphoric diestermonoamide", "リンdiestermonoamide");
    this.put("Phosphoric monoesterdiamide", "リンmonoesterdiamide");
    this.put("Phosphoric acid derivative", "リン酸誘導体");
    this.put("Phosphinic acid", "ホスフィン酸");
    this.put("Phosphinic ester", "ホスフィン酸エステル");
    this.put("Phosphinic amide", "ホスフィンアミド");
    this.put("Phosphinic acid derivative", "ホスフィン酸誘導体");
    this.put("Phosphonous acid", "亜ホスホン酸");
    this.put("Phosphonous monoester", "亜ホスホン酸モノエステル");
    this.put("Phosphonous diester", "亜ホスホン酸ジエステル");
    this.put("Phosphonous monoamide", "亜ホスホンモノアミド");
    this.put("Phosphonous diamide", "亜ホスホンジアミド");
    this.put("Phosphonous esteramide", "亜ホスホンエステルアミド");
    this.put("Phosphonous derivatives", "亜ホスホンデリバティブ");
    this.put("Phosphinous acid", "亜ホスフィン酸");
    this.put("Phosphinous ester", "亜ホスフィン酸エステル");
    this.put("Phosphinous amide", "ホスフィンアミド");
    this.put("Phosphinous derivatives", "ホスフィン誘導体、");
    this.put("Quart silane", "クアルトシラン");
    this.put("Non-quart silane", "非クォートシラン");
    this.put("Silylmonohalide", "Silylmonohalide");
    this.put("Het trialkylsilane", "ヘットトリアルキルシラン");
    this.put("Dihet dialkylsilane", "Dihetジアルキル");
    this.put("Trihet alkylsilane", "Trihetアルキルシラン");
    this.put("Silicic acid derivative", "ケイ酸誘導体");
    this.put("Trialkylborane", "トリアルキルボラン");
    this.put("Boric acid derivatives", "ホウ酸誘導体");
    this.put("Boronic acid derivative", "ボロン酸誘導体");
    this.put("Borohydride", "ホウ化水素");
    this.put("Quaternary boron", "第四紀ホウ素");
    this.put("Heterocyclic", "複素環式の");
    this.put("Epoxide", "エポキシド");
    this.put("NH aziridine", "NHアジリジン");
    this.put("Spiro", "スピロ");
    this.put("Annelated rings", "縮環リング");
    this.put("Bridged rings", "架橋環");
    this.put("Sugar pattern 1", "シュガーパターン1");
    this.put("Sugar pattern 2", "シュガーパターン2");
    this.put("Sugar pattern combi", "シュガーパターンコンビ");
    this.put("Sugar pattern 2 reducing", "シュガーパターン2削減");
    this.put("Sugar pattern 2 alpha", "シュガーパターン2アルファ");
    this.put("Sugar pattern 2 beta", "シュガーパターン2ベータ");
    this.put("Mixed anhydrides", "混合無水物");
    this.put("Halogen on hetero", "ヘテロのハロゲン");
    this.put("Halogen with multiple substitutions",
             "複数の置換を有するハロゲン");
    this.put("Trifluoromethyl", "トリフルオロメチル");
  }

}
