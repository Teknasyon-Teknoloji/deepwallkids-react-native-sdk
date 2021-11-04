
#import "RNDeepWallKids.h"
#import <DeepWallKids/DeepWallKids.h>
#import <React/RCTUtils.h>
#import "RNDeepWallKidsEmitter.h"
#import "DWRUserProperties.h"

@interface RNDeepWallKids() <DeepWallKidsNotifierDelegate>

@property (class) BOOL hasInitialized;

@end

@implementation RNDeepWallKids

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

static BOOL hasInitialized;

+ (void)setHasInitialized:(BOOL)status {
    hasInitialized = status;
}

+ (BOOL)hasInitialized {
    return hasInitialized;
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(initialize:(NSString *)apiKey environment:(int)environment)
{
    if ([RNDeepWallKids hasInitialized] == YES) {
        return;
    }

    [RNDeepWallKids setHasInitialized:YES];

	[[DeepWallKidsCore shared] observeEventsFor:self];

	DeepWallKidsEnvironment env;
	if (environment == 1) {
		env = DeepWallKidsEnvironmentSandbox;
	} else {
		env = DeepWallKidsEnvironmentProduction;
	}

	[DeepWallKidsCore initializeWithApiKey:apiKey environment:env];
}

RCT_EXPORT_METHOD(setUserProperties:(NSDictionary *)props)
{
	NSError *error;
	DWRUserProperties *dwProps = [[DWRUserProperties alloc] initWithDictionary:props error:&error];

	if (error != nil) {
		NSLog(@"[RNDeepWallKids] Failed to set user properties!");
		return;
	}

	[[DeepWallKidsCore shared] setUserProperties:[dwProps toDWObject]];
}

RCT_EXPORT_METHOD(updateUserProperties:(NSString *)country language:(NSString *)language environmentStyle:(int)environmentStyle)
{
	NSString *dwCountry = nil;
	if (country != nil) {
		dwCountry = [DeepWallKidsCountryManager getCountryByCode:country];
	}

	NSString *dwLanguage = nil;
	if (language != nil) {
		dwLanguage = [DeepWallKidsLanguageManager getLanguageByCode:language];
	}

	DeepWallKidsEnvironmentStyle dwEnvironmentStyle;
	if (environmentStyle != 0) {
		dwEnvironmentStyle = (DeepWallKidsEnvironmentStyle)environmentStyle;
	} else {
		dwEnvironmentStyle = [[DeepWallKidsCore shared] userProperties].environmentStyle;
	}

	[[DeepWallKidsCore shared] updateUserPropertiesCountry:dwCountry language:dwLanguage environmentStyle:dwEnvironmentStyle];
}


RCT_EXPORT_METHOD(requestPaywall:(NSString *)action extraData:(NSDictionary *)extraData)
{
	dispatch_async(dispatch_get_main_queue(), ^{
		UIViewController *view = RCTPresentedViewController();
		if (view == nil) {
			return;
		}

		[[DeepWallKidsCore shared] requestPaywallWithAction:action inView:view extraData:extraData];
	});
}


RCT_EXPORT_METHOD(sendExtraDataToPaywall:(NSDictionary *)extraData)
{
	[[DeepWallKidsCore shared] sendExtraDataToPaywall:extraData];
}


RCT_EXPORT_METHOD(closePaywall)
{
	[[DeepWallKidsCore shared] closePaywall];
}

RCT_EXPORT_METHOD(hidePaywallLoadingIndicator)
{
	[[DeepWallKidsCore shared] hidePaywallLoadingIndicator];
}

RCT_EXPORT_METHOD(validateReceipt:(int)type)
{
	PloutosValidationType validationType = (PloutosValidationType)type;
	[[DeepWallKidsCore shared] validateReceiptForType:validationType];
}


#pragma mark - DeepWallKidsNotifierDelegate

- (void)deepWallKidsPaywallRequested {
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallRequested" dataEncoded:@{}];
}

- (void)deepWallKidsPaywallResponseReceived {
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallResponseReceived" dataEncoded:@{}];
}

- (void)deepWallKidsPaywallResponseFailure:(DeepWallKidsPaywallResponseFailedModel *)event {
	NSDictionary *data = @{
		@"errorCode": event.errorCode ?: @"",
		@"reason": event.reason ?: @""
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallResponseFailure" dataEncoded:data];
}

- (void)deepWallKidsPaywallOpened:(DeepWallKidsPaywallOpenedInfoModel *)event {
	NSDictionary *data = @{
		@"pageId": @(event.pageId)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallOpened" dataEncoded:data];
}

- (void)deepWallKidsPaywallNotOpened:(DeepWallKidsPaywallNotOpenedInfoModel *)event {
	NSDictionary *data = @{
		@"pageId": @(event.pageId)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallNotOpened" dataEncoded:data];
}

- (void)deepWallKidsPaywallActionShowDisabled:(DeepWallKidsPaywallActionShowDisabledInfoModel *)event {
	NSDictionary *data = @{
		@"pageId": @(event.pageId)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallActionShowDisabled" dataEncoded:data];
}

- (void)deepWallKidsPaywallClosed:(DeepWallKidsPaywallClosedInfoModel)event {
	NSDictionary *data = @{
		@"pageId": @(event.pageId)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallClosed" dataEncoded:data];
}

- (void)deepWallKidsPaywallExtraDataReceived:(DeepWallKidsExtraDataType)event {
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallExtraDataReceived" dataEncoded:event];
}


- (void)deepWallKidsPaywallPurchasingProduct:(DeepWallKidsPaywallPurchasingProduct *)event {
	NSDictionary *data = @{
		@"productCode": event.productCode
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallPurchasingProduct" dataEncoded:data];
}

- (void)deepWallKidsPaywallPurchaseSuccess:(DeepWallKidsValidateReceiptResult)event {
	NSDictionary *data = @{
		@"type": @((int)event.type),
		@"result": event.result != nil ? [event.result toDictionary] : @{}
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallPurchaseSuccess" dataEncoded:data];
}


- (void)deepWallKidsPaywallPurchaseFailed:(DeepWallKidsPurchaseFailedModel)event {
	NSDictionary *data = @{
		@"productCode": event.productCode ?: @"",
		@"reason": event.reason ?: @"",
		@"errorCode": event.errorCode ?: @"",
		@"isPaymentCancelled": @(event.isPaymentCancelled)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallPurchaseFailed" dataEncoded:data];
}

- (void)deepWallKidsPaywallRestoreSuccess {
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallRestoreSuccess" dataEncoded:@{}];
}

- (void)deepWallKidsPaywallRestoreFailed:(DeepWallKidsRestoreFailedModel)event {
	NSDictionary *data = @{
		@"reason": @((int)event.reason),
		@"errorCode": event.errorCode ?: @"",
		@"errorText": event.errorText ?: @"",
		@"isPaymentCancelled": @(event.isPaymentCancelled)
	};
	[[RNDeepWallKidsEmitterSingleton sharedManager] sendEventWithName:@"deepWallKidsPaywallRestoreFailed" dataEncoded:data];
}

@end

