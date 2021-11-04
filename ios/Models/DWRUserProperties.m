//
//  DWRUserProperties.m
//  RNDeepWallKids
//
//  Created by Burak Yalcin on 10.11.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "DWRUserProperties.h"

@implementation DWRUserProperties

- (DeepWallKidsUserProperties *)toDWObject {
	NSString *dwCountry = [DeepWallKidsCountryManager getCountryByCode:self.country];
	NSString *dwLanguage = [DeepWallKidsLanguageManager getLanguageByCode:self.language];
	
	DeepWallKidsEnvironmentStyle dwEnvironmentStyle;
	if (self.environmentStyle != nil) {
		dwEnvironmentStyle = (DeepWallKidsEnvironmentStyle)self.environmentStyle.integerValue;
	} else {
		dwEnvironmentStyle = DeepWallKidsEnvironmentStyleAutomatic;
	}
	
	return [[DeepWallKidsUserProperties alloc] initWithUuid:self.uuid country:dwCountry language:dwLanguage environmentStyle:dwEnvironmentStyle];
}

@end
