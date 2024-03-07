//
//  HistoryListTileSwiftUIView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 16/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct HistoryListTile: View{
  let url: String
  let isFavorite: Bool
  let date: Date
  let favoriteButtonOnTap: () -> Void
  let historyListTileOnTap: () -> Void
  
  func formattedDate() -> String{
    let formatter = DateFormatter()
    formatter.dateFormat = "E d MMM yyyy"
    return formatter.string(from: date)
  }
  
  var favoriteIcon: some View {
    let iconName = (!isFavorite ? "favorite_icon":"favorite_icon_filled")
    
    return Image(iconName)
      .resizable()
      .scaledToFill()
      .frame(width: 32, height: 32)
      .onTapGesture {
        favoriteButtonOnTap()
      }
  }
  
  
  var body: some View{
    VStack{
      HStack{
        HStack{
          VStack(alignment: .leading){
            Text(url)
              .font(.system(size: 14, weight: .regular))
              .lineSpacing(21)
              .foregroundColor(Color("primary-text"))
              .frame(alignment: .leading)
            Text(formattedDate())
              .font(.system(size: 14, weight: .light))
              .foregroundColor(Color("gray-primary"))
              .frame(alignment: .leading)
          }
          Spacer()
        }
        .contentShape(Rectangle())
        .onTapGesture {
          historyListTileOnTap()
        }
        favoriteIcon
      }
      .padding(.bottom,6)
      Divider()
    }
    .padding(.vertical,6)
    .padding(.horizontal,15)
  }
}
