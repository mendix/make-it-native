//
//  History.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 13/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//
import UIKit
import SwiftUI
import UniformTypeIdentifiers

struct HistoryView: View {
  @StateObject private var historyStore = HistoryStore()
  
  @State private var counter = 0
  
  
  init(counter: Int = 0) {
    self.counter = counter
  }
  
  // Sorts from favorite to unfavorite.
  func getFavoritedOrder(_ items: [HistoryItem])->[HistoryItem]{
    return items.sorted { $0.isFavorite && !$1.isFavorite }
  }
  
  func historyItemOnTap(item:HistoryItem){
    // Navigate to Home Screen.
    TabViewController().setSelectedIndex(index: 0)
    // Set textfields value with the url of the history item.
    AppState.shared.setUrl(val: item.url)
  }
  
  var body: some View {
    VStack{
      AppBarContent()
      
      ScrollView(.vertical, showsIndicators: false) {
        VStack{
          if historyStore.historyList.isEmpty {
            Text("No apps have been launched yet.")
              .padding(.top,25)
          } else {
            ForEach(getFavoritedOrder(historyStore.historyList)) { item in
              HistoryListTile(
                url: item.url,
                isFavorite: item.isFavorite,
                date: item.date,
                favoriteButtonOnTap: {
                  historyStore.toggleFavoriteItem(historyItem: item)
                },
                historyListTileOnTap: {
                  historyItemOnTap(item: item)
                }
              )
            }
          }
        }
      }
      .onAppear{
        _ = historyStore.loadHistoryItems()
        UIScrollView.appearance().bounces = false
      }
    }
    .edgesIgnoringSafeArea(.top)
  }
}


struct History_Previews: PreviewProvider {
  static var previews: some View {
    HistoryView()
  }
}

