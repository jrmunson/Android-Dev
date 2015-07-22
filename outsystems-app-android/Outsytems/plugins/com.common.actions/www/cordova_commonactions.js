/** 
	OutSystems - Mobile Services
	Vitor Oliveira - 12/02/2015

*/
function initCordova() {
    document.addEventListener('deviceready', this.onDeviceReady, false);
}

function logOut (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "CommonActionsPlugin", "logOut", [fileName]);
}

function closeTutorial (successCallback, errorCallback) {
    cordova.exec (successCallback, errorCallback, "CommonActionsPlugin", "closeTutorial", [urlVideo]);
}