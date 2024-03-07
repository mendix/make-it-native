//
//  HelpListTile.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct HelpListTile: View {
    @Environment(\.colorScheme) var colorScheme
    let message: String
  
  func buttonColor()->Color{
    return Color("brand-primary")
  }
    
  var body: some View {
      VStack{
        Text(message)
          .foregroundColor(
            buttonColor()
          )
          .frame(maxWidth: .infinity)
      }
      .padding(.vertical,12)
      .overlay(
        RoundedRectangle(cornerRadius: 4)
          .stroke(buttonColor(), lineWidth: 1.25)
      )
      .padding(.horizontal, 20)
      .padding(.vertical,7)
    }
}

