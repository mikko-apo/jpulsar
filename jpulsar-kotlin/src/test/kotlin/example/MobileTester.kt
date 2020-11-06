package example

class MobileTester(private val mobileClient: MobileClient) {
    fun sell(testSellId: String) {
        mobileClient.server.addTx(testSellId)
    }
}