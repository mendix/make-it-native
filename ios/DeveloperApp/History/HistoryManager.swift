//
//  HistoryManager.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 21/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import Foundation


class HistoryStore: ObservableObject{
  let historyKey = "history.data"
  let maximumHistoryItemNumber = 10 // history can not store more items than this number.
  
  @Published var historyList: [HistoryItem] = []
  
  init() {
    self.historyList = loadHistoryItems()
  }
  
  func countFavorites() -> Int {
    return historyList.filter { $0.isFavorite }.count
  }
  
  func loadHistoryItems() -> [HistoryItem] {
    let userDefaults = UserDefaults.standard
    if let savedData = userDefaults.object(forKey: historyKey) as? Data {
      do{
        let items = try JSONDecoder().decode([HistoryItem].self, from: savedData)
        self.historyList = items
        return items
      } catch {
        self.historyList = []
        return []
      }
    }
    self.historyList = []
    return []
  }
  
  func checkUrlExist(url: String) -> Bool {
    let items = loadHistoryItems()
    return items.contains { $0.url == url }
  }
  
  func saveHistoryItem(historyItem: HistoryItem) {
    // If the url is already added, do not add again, update its time.
    if checkUrlExist(url: historyItem.url){
      updateHistoryItemDate(historyItem: historyItem)
      return
    }
    
    // If favorite count equals to maximum history item number, do not add.
    if countFavorites() == maximumHistoryItemNumber {
      return
    }
    var items = loadHistoryItems()
    
    if items.count == maximumHistoryItemNumber, let deleteIndex = items.lastIndex(where: { !$0.isFavorite }) {
      items.remove(at: deleteIndex)
    }
    
    items.insert(historyItem, at: 0)
    do{
      let encodedData = try JSONEncoder().encode(items)
      let userDefaults = UserDefaults.standard
      userDefaults.set(encodedData, forKey: historyKey)
      self.historyList = items
    } catch{
      print("Error on saving history Item")
    }
  }
  
  func toggleFavoriteItem(historyItem: HistoryItem){
    let items = loadHistoryItems()
    
    if let itemIndex = items.firstIndex(where: { $0.id == historyItem.id }) {
      items[itemIndex].isFavorite.toggle()
      updateHistoryItems(items)
      self.historyList = items
    }
  }
  
  func updateHistoryItemDate(historyItem: HistoryItem){
    let items = loadHistoryItems()
    
    if let itemIndex = items.firstIndex( where: { $0.id == historyItem.id } ) {
      items[itemIndex].date = Date()
      updateHistoryItems(items)
      self.historyList = items
    }
  }
  
  func updateHistoryItems(_ items: [HistoryItem]){
    do{
      let encodedData = try JSONEncoder().encode(items)
      let userDefaults = UserDefaults.standard
      userDefaults.set(encodedData, forKey: historyKey)
    } catch{
      print("Error on saving history Item")
    }
  }
}

class HistoryItem: Identifiable, Codable{
  let id: UUID
  let url: String
  var isFavorite: Bool
  var date: Date
  
  init(id: UUID = UUID(), url: String) {
    self.id = id
    self.url = url
    self.isFavorite = false
    self.date = Date()
  }
}
