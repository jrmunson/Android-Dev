//
//  CommonActionsPlugin.h
//  OutSystems
//
//  Created by Vitor Oliveira on 26/11/14.
//
//

#import <Cordova/CDVPlugin.h>
#import "OutSystemsAppDelegate.h"

@interface CommonActionsPlugin : CDVPlugin

- (OutSystemsAppDelegate*)appDelegate;
- (void)logOut:(CDVInvokedUrlCommand*)command;
- (void)closeTutorial:(CDVInvokedUrlCommand*)command;

@end
