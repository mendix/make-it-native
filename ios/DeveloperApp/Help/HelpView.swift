//
//  HelpSwiftUIView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 15/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct HelpView: View {
  @State private var showingTutorialSheet = false

  var body: some View {
    VStack{
      LargeLogoImage(showVersion: true)
      
      ScrollView(.vertical,showsIndicators: false){
        Spacer().frame(height: 25)
        
        HelpListTile(message: "How to use Make it Native app")
          .onTapGesture {
            showingTutorialSheet.toggle()
          }
          .fullScreenCover(isPresented: $showingTutorialSheet){
            OnboardingView(closeOnboarding: {
              showingTutorialSheet.toggle()
            })
          }
        
        HelpListTile(message: "Forum")
          .onTapGesture {
            navigateToForum()
          }
        
        HelpListTile(message: "Mendix Slack Community")
          .onTapGesture {
            navigateToSlackCommunity()
          }
        
        HelpListTile(message: "Troubleshooting")
          .onTapGesture {
            navigateToTroubleshooting()
          }
        
        HelpListTile(message: "Mobile Documentation")
          .onTapGesture {
            navigateToMobileDocumentation()
          }
      }
      .onAppear{
        UIScrollView.appearance().bounces = false
      }
    }.edgesIgnoringSafeArea(.top)
  }
  
  func navigateToHowtos() {
    if let url = getUrlFromBundle("TutorialResourceUrl") {
      navigateTo(url)
    }
  }
  
  func navigateToForum() {
    if let url = getUrlFromBundle("ForumUrl") {
      navigateTo(url)
    }
  }
  
  func navigateToSlackCommunity(){
    if let url = getUrlFromBundle("SlackCommunityUrl") {
      navigateTo(url)
    }
  }
  
  func navigateToTroubleshooting(){
    if let url = getUrlFromBundle("TroubleShootingUrl") {
      navigateTo(url)
    }
  }
  
  func navigateToMobileDocumentation(){
    if let url = getUrlFromBundle("MobildeDocsUrl") {
      navigateTo(url)
    }
  }
}
