//
//  DWRUserProperties.h
//  RNDeepWallKids
//
//  Created by Burak Yalcin on 10.11.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <DeepWallKids/DeepWallKids.h>

NS_ASSUME_NONNULL_BEGIN

@interface DWRUserProperties : JSONModel

@property (nonatomic) NSString *uuid;
@property (nonatomic) NSString *country;
@property (nonatomic) NSString *language;
@property (nonatomic) NSNumber<Optional> *environmentStyle;

- (DeepWallKidsUserProperties *)toDWObject;

@end

NS_ASSUME_NONNULL_END
