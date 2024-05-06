//
//  SampleAppCard.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct SampleAppCard: View {
  let backgroundImage: String
  let name: String
  
  var body: some View {
    VStack{
      Image(backgroundImage)
        .resizable()
        .cornerRadius(10)
        .aspectRatio(contentMode: .fit)
      Text(name)
        .font(.system(size: 12, weight: .regular))
        .frame(maxWidth: .infinity,alignment: .leading)
    }.padding(.bottom,5)
  }
}
  
