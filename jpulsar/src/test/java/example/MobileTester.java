package example;

import example.main.MobileClient;

public class MobileTester {
    private MobileClient mobileClient;

    public MobileTester(MobileClient mobileClient) {
        this.mobileClient = mobileClient;
    }

    public void sell(String testSellId) {
        mobileClient.server.addTx(testSellId);
    }
}
