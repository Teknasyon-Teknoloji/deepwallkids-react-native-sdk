//
//  RNDeepWallKidsEmitter.m
//  RNDeepWallKids
//
//  Created by Burak Yalcin on 9.11.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "RNDeepWallKidsEmitter.h"
#import <React/RCTUtils.h>

@interface RNDeepWallKidsEmitter()

@property (nonatomic) BOOL hasListener;
@property (nonatomic) UIViewController *oldWindow;

@end

@implementation RNDeepWallKidsEmitter

RCT_EXPORT_MODULE();

static RNDeepWallKidsEmitter *sharedManager = nil;

+ (instancetype)sharedEmitter {
	return sharedManager;
}

+ (void)setSharedEmitter:(RNDeepWallKidsEmitter *)emitter {
	sharedManager = emitter;
}

- (instancetype)init
{
	self = [super init];
	if (self) {

		if ([RNDeepWallKidsEmitter sharedEmitter] == nil || [RNDeepWallKidsEmitter sharedEmitter].oldWindow == nil || [RNDeepWallKidsEmitter sharedEmitter].oldWindow == RCTPresentedViewController()) {
			self.oldWindow = RCTPresentedViewController();
			[RNDeepWallKidsEmitter setSharedEmitter:self];
		}
	}
	return self;
}

- (void)startObserving {
	self.hasListener = YES;
}

- (void)stopObserving {
	self.hasListener = NO;
}

- (NSArray<NSString *> *)supportedEvents {
	return @[ @"DeepWallKidsEvent" ];
}

+ (BOOL)requiresMainQueueSetup {
	return YES;
}

@end



@implementation RNDeepWallKidsEmitterSingleton

+ (instancetype)sharedManager {
	static RNDeepWallKidsEmitterSingleton *sharedManager = nil;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		sharedManager = [[self alloc] init];
	});
	return sharedManager;
}


- (void)sendEventWithName:(NSString *)name data:(JSONModel *)data {
	NSDictionary *encodedData = [data toDictionary];
	[self sendEventWithName:name dataEncoded:encodedData];
}

- (void)sendEventWithName:(NSString *)name dataEncoded:(NSDictionary *)encodedData {
	if([RNDeepWallKidsEmitter sharedEmitter] == nil || [RNDeepWallKidsEmitter sharedEmitter].bridge == nil || [RNDeepWallKidsEmitter sharedEmitter].hasListener == NO) {
		return;
	}

	[[RNDeepWallKidsEmitter sharedEmitter] sendEventWithName:@"DeepWallKidsEvent" body:@{
		@"event": name,
		@"data": encodedData
	}];
}



/*

 - (void)sendEventWithType:(DeepWallEventListenerToReactModelType)type data:(JSONModel *)data {
	 NSDictionary *encodedData = [data toDictionary];
	 [self sendEventWithType:type dictionary:encodedData];
 }

 - (void)sendEventWithType:(DeepWallEventListenerToReactModelType)type dictionary:(NSDictionary *)encodedData {

	 if(self.emitter == nil || self.emitter.bridge == nil) {
		 return;
	 }

	 [self.emitter sendEventWithName:@"ClientEvent" body:@{
		 @"type": @((int)type),
		 @"data": encodedData
	 }];
 }
 */


@end
