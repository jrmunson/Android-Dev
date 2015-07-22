cordova.define("com.os.mobile.facebooktoken.FacebookToken", function(require, exports, module) { /** 
	OutSystems - Mobile Services
	João Gonçalves - 28/04/2015
*/
function FacebookToken() {
}

exports.saveToken = function (successCallback, errorCallback, token, username) {
    cordova.exec(successCallback, errorCallback, "FacebookToken", "saveToken", [token, username]);
};
});
