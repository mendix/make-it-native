//
//  TroubleShootSheet.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

// This view for showing & troubleshooting Metro & RunTime Error.
struct TroubleshootView: View {
  
  // Launching function will be handled in HomeViewController.
  let backOnTap: () -> Void
  let primaryButtonOnTap: () -> Void
  let secondaryButtonOnTap: () -> Void
  
  let primaryButtonTitle: String
  let secondaryButtonTitle: String
  let title: String
  let description:String
  let instructions: String
  
  init(
    primaryButtonTitle: String,
    secondaryButtonTitle: String,
    title: String,
    description:String,
    instructions: String,
    backOnTap:@escaping () -> Void,
    primaryButtonOnTap: @escaping () -> Void,
    secondaryButtonOnTap: @escaping () -> Void
  ) {
    self.backOnTap = backOnTap
    self.primaryButtonOnTap = primaryButtonOnTap
    self.secondaryButtonOnTap = secondaryButtonOnTap
    self.primaryButtonTitle = primaryButtonTitle
    self.secondaryButtonTitle = secondaryButtonTitle
    self.title = title
    self.description = description
    self.instructions = instructions
  }
  
  
  var body: some View {
    VStack{
      Spacer()
        .frame(height: 15)
      
      HStack{
        Image(systemName: "xmark.circle.fill")
          .resizable()
          .foregroundColor(Color("gray-primary"))
          .frame(width: 30,height: 30)
          .padding(.trailing, 6)
          .onTapGesture {
            backOnTap()
          }
      }
      .frame(maxWidth: .infinity, alignment: .trailing)
      
      Spacer()
        .frame(height: 15)
      
      Image("alert_triangle")
        .resizable()
        .frame(width: 36, height: 36)
        .aspectRatio(contentMode: .fit)
        .scaledToFit()
        .padding(.bottom, 15)
      
      VStack{
        Text(title)
          .font(.system(size: 18,weight: .semibold))
          .frame(maxWidth: .infinity, alignment: .leading)
          .padding(.bottom,15)
        
        HStack{
          Text(description)
            .font(.body)
            .padding(.bottom,12)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.bottom,25)
        
        
        VStack{
          Text(instructions)
            .frame(maxWidth: .infinity, alignment: .leading)
            .font(.body)
            .padding(.bottom,8)
        }
        
      }
      .padding(.horizontal,15)
      Spacer()
      
      HelpListTile(message: primaryButtonTitle)
        .onTapGesture {
          primaryButtonOnTap()
        }
      HelpListTile(message: secondaryButtonTitle)
        .onTapGesture {
          secondaryButtonOnTap()
        }
    }
    .padding(.bottom,25)
    .padding(.horizontal,16)
    
  }
}
