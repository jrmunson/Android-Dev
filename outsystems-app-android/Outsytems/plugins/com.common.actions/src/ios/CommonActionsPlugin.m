//
//  CommonActionsPlugin.m
//  OutSystems
//
//  Created by Vitor Oliveira on 26/11/14.
//
//

#import "CommonActionsPlugin.h"
#import "ApplicationViewController.h"

@implementation CommonActionsPlugin

- (void)logOut:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.appDelegate.navigationControllerHelper popViewControllerAnimated:YES];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)closeTutorial:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    
    ApplicationViewController *applicationViewController = (ApplicationViewController *)self.appDelegate.window.rootViewController;
    //if (![applicationViewController isKindOfClass:[ApplicationViewController class]]){
     //   return;
    //}
    [applicationViewController dismissViewControllerAnimated:NO completion:^{
    
    }];
    
    NSLog(@"Close View Controller");
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (OutSystemsAppDelegate *)appDelegate {
    return (OutSystemsAppDelegate*)[[UIApplication sharedApplication] delegate];
}

@end
