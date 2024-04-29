//
//  SampleAppsSwiftUIView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI
import UIKit

struct SampleAppsSwiftUIView: View {
  private let config = SampleAppsConfig()
  private var sampleApps = [SampleApp]()
  let onTap: (_ app:SampleApp) -> Void // Sample Apps' Card OnTap
  
  init(onTap:@escaping (_ app: SampleApp) -> Void) {
    self.onTap = onTap
    sampleApps = config.read();
  }
  
  var items: [GridItem] {
    // Grid Item's constraints
    Array(repeating: .init(.adaptive(minimum: 120)), count: 2)
  }
  
  var body: some View {
    VStack{
      AppBarContent()
      
      ScrollView(.vertical, showsIndicators: false) {
        LazyVGrid(columns: items, spacing: 12) {
          ForEach(sampleApps) { item in
            SampleAppCard(
              backgroundImage: item.backgroundImageName!,
              name: item.name
            ).onTapGesture {
              showcaseAppOnTap(item)
            }
          }
        }
        .padding(.bottom, 50)
      }
      .padding(.horizontal)
    }
    .edgesIgnoringSafeArea(.top)
  }
  
  func showcaseAppOnTap(_ sampleApp:SampleApp){
    onTap(sampleApp)
  }
}
