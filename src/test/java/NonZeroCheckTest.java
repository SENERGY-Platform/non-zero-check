import org.infai.ses.senergy.models.DeviceMessageModel;
import org.infai.ses.senergy.models.MessageModel;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.infai.ses.senergy.operators.nonzerocheck.NonZeroCheck;
import org.infai.ses.senergy.testing.utils.JSONHelper;
import org.infai.ses.senergy.utils.ConfigProvider;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;

public class NonZeroCheckTest {
    @Test
    public void productTest() throws Exception {
        Config config = new Config(new JSONHelper().parseFile("config.json").toString());
        JSONArray messages = new JSONHelper().parseFile("messages.json");
        String topicName = config.getInputTopicsConfigs().get(0).getName();
        ConfigProvider.setConfig(config);
        Message message = new Message();
        MessageModel model = new MessageModel();
        OperatorInterface testOperator = new NonZeroCheck();
        message.addInput("expectValue");
        message.addInput("expectTS");
        testOperator.configMessage(message);
        for (Object msg : messages) {
            DeviceMessageModel deviceMessageModel = JSONHelper.getObjectFromJSONString(msg.toString(), DeviceMessageModel.class);
            assert deviceMessageModel != null;
            model.putMessage(topicName, Helper.deviceToInputMessageModel(deviceMessageModel, topicName));
            message.setMessage(model);
            testOperator.run(message);
            Assert.assertEquals(message.getInput("expectValue").getValue(), message.getMessage().getOutputMessage().getAnalytics().get("product"));
            Assert.assertEquals(message.getInput("expectTS").getString(), message.getMessage().getOutputMessage().getAnalytics().get("lastTimestamp"));
        }
    }
}
