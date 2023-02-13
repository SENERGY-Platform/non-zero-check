/*
 * Copyright 2018 InfAI (CC SES)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.infai.ses.senergy.operators.nonzerocheck;

import org.infai.ses.senergy.exceptions.NoValueException;
import org.infai.ses.senergy.operators.BaseOperator;
import org.infai.ses.senergy.operators.FlexInput;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NonZeroCheck extends BaseOperator {

    private Map<String, Double> map1;
    private Map<String, Double> map2;
    private boolean debug;

    public NonZeroCheck(){
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        debug = Boolean.parseBoolean(Helper.getEnv("DEBUG", "false"));
    }

    @Override
    public void run(Message message) {
        FlexInput value1Input = message.getFlexInput("value1");
        FlexInput value2Input = message.getFlexInput("value2");
        FlexInput timeInput  = message.getFlexInput("timestamp");
        String timestamp;
        try {
            timestamp = timeInput.getString();
        } catch (NoValueException e) {
            e.printStackTrace();
            return;
        }
        Set<Map.Entry<String, Double>> entries1 = value1Input.getFilterIdValueMap(Double.class).entrySet();
        for (Map.Entry<String, Double> entr: entries1) {
            map1.put(entr.getKey(), entr.getValue());
        }
        Set<Map.Entry<String, Double>> entries2 = value2Input.getFilterIdValueMap(Double.class).entrySet();
        for (Map.Entry<String, Double> entr: entries2) {
            map2.put(entr.getKey(), entr.getValue());
        }

        boolean anomaly_check = map1.values().stream().mapToDouble(v -> v).allMatch(i -> i > 0);
        boolean quantile_check = map2.values().stream().mapToDouble(v -> v).allMatch(i -> i > 0);
        

        if (anomaly_check == true && quantile_check == true) {
            message.output("value", (int) 1);
        }
        else {
            message.output("value", (int) 0);
        }

        if (debug) {
            for (Map.Entry<String, Double> entr: map1.entrySet()) {
                System.out.println(entr.getKey() + ": " + entr.getValue());
            }
            for (Map.Entry<String, Double> entr: map2.entrySet()) {
                System.out.println(entr.getKey() + ": " + entr.getValue());
            }
        }

        message.output("lastTimestamp", timestamp);
    }

    @Override
    public Message configMessage(Message message) {
        message.addFlexInput("value1");
        message.addFlexInput("value2");
        message.addFlexInput("timestamp");
        return message;
    }
}
