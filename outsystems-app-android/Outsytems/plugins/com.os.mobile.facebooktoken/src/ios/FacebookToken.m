//
//  FacebookToken.h
//  OutSystems
//
//  Created by João Gonçalves on 27/04/15.
//
//

#import "FacebookToken.h"

@implementation FacebookToken

- (void)saveToken:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    
    NSString * tokenArg = [command.arguments objectAtIndex:0];
    NSString * usernameArg = [command.arguments objectAtIndex:1];
    
    if(tokenArg == nil){
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } else if(usernameArg == nil){
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    } else {
        NSManagedObjectContext *context = nil;
        id delegate = self.appDelegate;
        if ([delegate performSelector:@selector(managedObjectContext)]) {
            context = [delegate managedObjectContext];
        }
        
        NSManagedObjectContext *managedObjectContext = context;
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] initWithEntityName:@"Infrastructure"];
        NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"lastUsed" ascending:NO];;
        
        [fetchRequest setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor, nil]];
        [fetchRequest setPredicate:[NSPredicate predicateWithFormat:@"hostname == %@", HOSTNAME]];
        NSMutableArray *environments = [[managedObjectContext executeFetchRequest:fetchRequest error:nil] mutableCopy];
        Infrastructure *infrastructure = [environments objectAtIndex:0];
        
        infrastructure.token = [tokenArg copy];
        infrastructure.username = [usernameArg copy];
        infrastructure.password = @"";
        
        NSError *error = nil;
        if (![infrastructure.managedObjectContext save:&error]) {
            NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
        }
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (OutSystemsAppDelegate *)appDelegate {
    return (OutSystemsAppDelegate*)[[UIApplication sharedApplication] delegate];
}

@end
