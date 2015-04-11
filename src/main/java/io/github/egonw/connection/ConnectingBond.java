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
 * @file   ConnectingBond.java
 * @author Volker Sorge <sorge@zorkstone>
 * @date   Wed Feb 11 00:27:03 2015
 * 
 * @brief  Class of connecting bonds.
 * 
 * 
 */

//
package io.github.egonw.connection;

import io.github.egonw.analysis.RichStructureHelper;
import io.github.egonw.sre.SreNamespace;
import io.github.egonw.sre.SreElement;

/**
 * Class of connecting bonds.
 */

public class ConnectingBond extends Connection {

    public ConnectingBond(String connector, String connected) {
        super(connector, connected);
    }


    @Override
    public ConnectionType getType() {
        return ConnectionType.CONNECTINGBOND;
    }

    @Override
    public SreNamespace.Tag tag() {
        return SreNamespace.Tag.CONNECTINGBOND;
    }

    @Override
    public SreElement annotation() {
        String connected = this.getConnected();
        SreNamespace.Tag type = RichStructureHelper.isAtom(connected) ?
            SreNamespace.Tag.ATOM : SreNamespace.Tag.ATOMSET;
        return new SreElement(this.tag(),
                              new SreElement(SreNamespace.Tag.BOND, this.getConnector()),
                              new SreElement(type, connected));
    }

}
