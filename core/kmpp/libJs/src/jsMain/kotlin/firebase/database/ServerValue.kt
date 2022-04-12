package firebase.database

import kotlin.js.Json
import kotlin.js.json

/** From firebase npm source code:
    Database.ServerValue = {
        TIMESTAMP: {
            '.sv': 'timestamp'
        },
        increment: function (delta) {
            return {
                '.sv': {
                    'increment': delta
                }
            };
        }
    };
*/

object ServerValue {
    val TIMESTAMP: Json = json(".sv" to "timestamp")
    fun increment(delta: Int): Json {
        return json(".sv" to json("increment" to delta))
    }
}