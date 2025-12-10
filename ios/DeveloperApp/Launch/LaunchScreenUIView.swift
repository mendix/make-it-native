//
//  LaunchScreenView.swift
//  DeveloperApp
//
//  Created by Diego Antonelli on 09/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct LaunchScreenUIView: View {
  @EnvironmentObject private var launchScreenState: LaunchScreenStateManager
  
  @State private var firstAnimation = false
  @State private var secondAnimation = false
  @State private var startFadeoutAnimation = false
  
  @State private var loadingText = UIApplication.Constants.launchTexts.randomElement() ?? ""
  
  @ViewBuilder
  private var image: some View {
    Image("Logo Icon")
      .resizable()
      .scaledToFill()
      .frame(width: 100, height: 100)
      .opacity(firstAnimation ? 1 : 0)
      .clipped()
  }
  
  private let animationTimer = Timer
    .publish(every: 1, on: .current, in: RunLoop.Mode.common)
    .autoconnect()
  
  var body: some View {
    ZStack(alignment: .center) {
      Image(UIApplication.Constants.randomBackground)
        .resizable()
        .scaledToFill()
        .clipped()
        .edgesIgnoringSafeArea(.all)
      
      VStack{
        image
          .padding(.bottom,10)
        
        Text(loadingText)
          .font(.system(size: 20))
          .foregroundColor(.white)
          .fontWeight(.semibold)
          .onDisappear{
            loadingText = UIApplication.Constants.launchTexts.randomElement() ?? ""
          }
      }
      .padding(.top,-20)
      
    }.onReceive(animationTimer) { timerValue in
      updateAnimation()
    }.opacity(startFadeoutAnimation ? 0 : 1)
  }
  
  private func updateAnimation() { 
    switch launchScreenState.state {
    case .initialPointOfAnimation:
      withAnimation(.easeInOut(duration: 1)) {
        firstAnimation.toggle()
      }
    case .finalPointOfAnimation:
      if secondAnimation == false {
        withAnimation(.linear) {
          self.secondAnimation = true
          startFadeoutAnimation = true
        }
      }
    }
  }
}
struct LaunchScreenView_Previews: PreviewProvider {
  static var previews: some View {
    LaunchScreenUIView().environmentObject(LaunchScreenStateManager())
  }
}
