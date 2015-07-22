//
//  FacebookToken.m
//  OutSystems
//
//  Created by João Gonçalves on 27/04/15.
//
//

#import <Cordova/CDVPlugin.h>
#import "OutSystemsAppDelegate.h"

@interface FacebookToken : CDVPlugin

- (OutSystemsAppDelegate*)appDelegate;
- (void)saveToken:(CDVInvokedUrlCommand*)command;

@end
