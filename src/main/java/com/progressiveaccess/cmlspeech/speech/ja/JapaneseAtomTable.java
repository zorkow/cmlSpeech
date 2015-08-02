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
 * @file   JapaneseAtomTable.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Fri Jun 20 02:00:25 2014
 *
 * @brief  Singleton class to translate atom names in Japanese.
 *
 *
 */

//

package com.progressiveaccess.cmlspeech.speech.ja;

import com.progressiveaccess.cmlspeech.speech.AbstractAtomTable;


/**
 * Maps atom identifiers to their proper names.
 */

public final class JapaneseAtomTable extends AbstractAtomTable {

  private static final long serialVersionUID = 1L;

  public JapaneseAtomTable() {
    this.put("Ac", "アクチニウム");
    this.put("Al", "アルミニウム");
    this.put("Am", "アメリシウム");
    this.put("Sb", "アンチモン");
    this.put("Ar", "アルゴン");
    this.put("As", "ヒ素");
    this.put("At", "アスタチン");
    this.put("Ba", "バリウム");
    this.put("Bk", "バークリウム");
    this.put("Be", "ベリリウム");
    this.put("Bi", "ビスマス");
    this.put("Bh", "ボーリウム");
    this.put("B", "ホウ素");
    this.put("Br", "臭素");
    this.put("Cd", "カドミウム");
    this.put("Ca", "カルシウム");
    this.put("Cf", "カリホルニウム");
    this.put("C", "炭素");
    this.put("Ce", "セリウム");
    this.put("Cs", "セシウム");
    this.put("Cl", "塩素");
    this.put("Cr", "クロム");
    this.put("Co", "コバルト");
    this.put("Cn", "コペルニシウム");
    this.put("Cu", "銅");
    this.put("Cm", "キュリウム");
    this.put("Ds", "ダームスタチウム");
    this.put("Db", "ドブニウム");
    this.put("Dy", "ジスプロシウム");
    this.put("Es", "アインスタイニウム");
    this.put("Er", "エルビウム");
    this.put("Eu", "ユウロピウム");
    this.put("Fm", "フェルミウム");
    this.put("Fl", "フレロビウム");
    this.put("F", "フッ素");
    this.put("Fr", "フランシウム");
    this.put("Gd", "ガドリニウム");
    this.put("Ga", "カリウムガリウム");
    this.put("Ge", "ゲルマニウム");
    this.put("Au", "キン金");
    this.put("Hf", "ハフニウム");
    this.put("Hs", "ハッシウム");
    this.put("He", "ヘリウム");
    this.put("Ho", "ホルミウム");
    this.put("H", "水素");
    this.put("In", "インジウム");
    this.put("I", "ヨウ素");
    this.put("Ir", "イリジウム");
    this.put("Fe", "鉄");
    this.put("Kr", "クリプトン");
    this.put("La", "ランタン");
    this.put("Lr", "ローレンシウム");
    this.put("Pb", "鉛");
    this.put("Li", "リチウム");
    this.put("Lv", "リバモリウム");
    this.put("Lu", "ルテチウム");
    this.put("Mg", "マグネシウム");
    this.put("Mn", "マンガン");
    this.put("Mt", "マイトネリウム");
    this.put("Md", "メンデレビウム");
    this.put("Hg", "水銀");
    this.put("Mo", "モリブデン");
    this.put("Nd", "ネオジム");
    this.put("Ne", "ネオン");
    this.put("Np", "ネプツニウム");
    this.put("Ni", "ニッケル");
    this.put("Nb", "ニオブ");
    this.put("N", "窒素");
    this.put("No", "ノーベリウム");
    this.put("Os", "オスミウム");
    this.put("O", "酸素");
    this.put("Pd", "パラジウム");
    this.put("P", "リン");
    this.put("Pt", "白金");
    this.put("Pu", "プルトニウム");
    this.put("Po", "ポロニウム");
    this.put("K", "カリウムカリウム");
    this.put("Pr", "プラセオジム");
    this.put("Pm", "プロメチウム");
    this.put("Pa", "プロトアクチニウム");
    this.put("Ra", "ラジウム");
    this.put("Rn", "ラドン");
    this.put("Re", "レニウム");
    this.put("Rh", "ロジウム");
    this.put("Rg", "レントゲニウム");
    this.put("Rb", "ルビジウム");
    this.put("Ru", "ルテニウム");
    this.put("Rf", "ラザホージウム");
    this.put("Sm", "サマリウム");
    this.put("Sc", "スカンジウム");
    this.put("Sg", "シーボーギウム");
    this.put("Se", "セレン");
    this.put("Si", "ケイ素");
    this.put("Ag", "キン銀");
    this.put("Na", "ナトリウム");
    this.put("Sr", "ストロンチウム");
    this.put("S", "硫黄");
    this.put("Ta", "タンタル");
    this.put("Tc", "テクネチウム");
    this.put("Te", "テルル");
    this.put("Tb", "テルビウム");
    this.put("Tl", "タリウム");
    this.put("Th", "トリウム");
    this.put("Tm", "ツリウム");
    this.put("Sn", "スズ");
    this.put("Ti", "チタン");
    this.put("W", "タングステン");
    this.put("Uuo", "ウンウンオクチウム");
    this.put("Uup", "ウンウンペンチウム");
    this.put("Uus", "ウンウンセプチウム");
    this.put("Uut", "ウンウントリウム");
    this.put("U", "ウラン");
    this.put("V", "バナジウム");
    this.put("Xe", "キセノン");
    this.put("Yb", "イッテルビウム");
    this.put("Y", "イットリウム");
    this.put("Zn", "亜鉛");
    this.put("Zr", "ジルコニウム");
  }

}
