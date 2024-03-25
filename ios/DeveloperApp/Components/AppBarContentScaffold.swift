//
//  AppBarScaffold.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct AppBarContent: View{
  
  var body: some View {
    ZStack(alignment: .bottom){
      Image(uiImage: UIApplication.randomBackgroundImage())
        .resizable()
        .scaledToFill()
        .frame(height: 100)
        .clipped()
      
      SmallLogoImage()
        .offset(x: 0, y: -10)
      
    }
    
    .padding(.bottom, 6)
  }
}

