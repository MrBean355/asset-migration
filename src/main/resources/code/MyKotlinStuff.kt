package code

class MyKotlinStuff {

    fun doThings() {
        println("--> icn_alert      <--") // should be replaced
        println("--> my_icn_alert   <--") // should not be replaced
        println("--> icn_alert_2    <--") // should not be replaced
        println("--> my_icn_alert_2 <--") // should not be replaced
    }
}