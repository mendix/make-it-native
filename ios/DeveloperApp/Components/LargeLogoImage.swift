//
//  LargeBackgroundImage.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI
import UIKit

struct LargeLogoImage: View {
    let showVersion: Bool
  
    var body: some View {
      ZStack(alignment: .bottomLeading){
        Image(uiImage: UIApplication.randomBackgroundImage())
          .resizable()
          .scaledToFill()
          .frame(height: 250)
          .edgesIgnoringSafeArea(.all)
          .clipped()
          
        HStack{
          Image("Logo Icon")
            .resizable()
            .scaledToFill()
            .clipped()
            .frame(width: 50, height: 50)
          
          Spacer()
          
          if showVersion{
            Text(getVersionString())
              .fontWeight(.semibold)
              .foregroundColor(Color.white)
              .padding(.bottom, -20)
          }
        }
        .padding(.bottom,30)
        .padding(.horizontal,20)
      }
    }
}

struct LargeBackgroundImage_Previews: PreviewProvider {
    static var previews: some View {
      LargeLogoImage(showVersion: false)
    }
}
