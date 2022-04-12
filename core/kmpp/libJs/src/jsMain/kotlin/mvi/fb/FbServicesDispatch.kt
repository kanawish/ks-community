package mvi.fb

class FbServicesDispatch(private val fbServices: FbServices) {
   fun handle(event: FbAuthViewEvent) {
      when (event) {
         FbAuthViewEvent.SignOut ->
            fbServices.process(fbServices.signOutIntent)
      }
   }
}