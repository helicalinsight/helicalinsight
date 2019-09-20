/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.vf;

public class ChartUtils {

    public String array2csv(String[] chartTagMeasuresArray) throws NullPointerException {
        // TODO Auto-generated method stub
        if (chartTagMeasuresArray[0] == null) {
            return "";
        } else {
            int numberOfMeasures = chartTagMeasuresArray.length;
            StringBuilder Measures = new StringBuilder();
            if (numberOfMeasures == 1) {
                Measures.append("'").append(chartTagMeasuresArray[0]).append("'");
            } else {
                for (int i = 0; i < numberOfMeasures; i++) {
                    Measures.append("'").append(chartTagMeasuresArray[i]).append("'");
                    if (i != (numberOfMeasures - 1)) {
                        Measures.append(",");
                    }
                }
            }
            return Measures.toString();
        }
    }
}
