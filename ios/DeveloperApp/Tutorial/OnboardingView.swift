//
//  TutorialSwiftUiView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/07/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

class OnboardingStep{
  let title: String
  let message: String
  let image: String
  
  init(title:String, message:String, image: String) {
    self.title = title
    self.message = message
    self.image = image
  }
}

// Constant Onboarding Config
let onboardingConfig: [OnboardingStep] = [
  OnboardingStep(title: "Preview your app effortlessly. ", message: "Scan the SP QR code to instantly preview and debug your project.", image: "Scan QR"),
  OnboardingStep(title: "Seamless navigation at your fingertips.", message: "Restart your app with a simple tap of three fingers. Access the context menu by tapping and holding.", image: "Tap screen"),
  OnboardingStep(title: "The ‘History’ for hassle-free testing.", message: "Access your favorite previously visited URLs without the need to retype them.", image: "History"),
]

struct OnboardingView: View {
  let closeOnboarding: () -> Void
  @State var currentIndex = 0
  
  private func previousButtonOnTap(){
    if currentIndex > 0{
      currentIndex = currentIndex - 1
    }
  }
  
  private func nextButtonOnTap(){
    if currentIndex+1 == onboardingConfig.count {
      closeOnboarding()
      return
    }
    currentIndex = currentIndex + 1
  }
  
  
  var body: some View {
    ZStack{
      Image(UIApplication.Constants.randomBackground)
        .resizable()
        .scaledToFill()
        .edgesIgnoringSafeArea(.all)
      
      VStack{
        HStack{
          Spacer()
          Text(getVersionString())
            .fontWeight(.regular)
            .foregroundColor(Color.white)
            .padding(.top, 20)
            .padding(.trailing, 20)
        }
        .frame(width: UIScreen.main.bounds.width)
        Spacer()
      }
      
      ZStack{
        Color.white
        VStack{
          VStack{
            HStack{
              Image(onboardingConfig[currentIndex].image)
                .resizable()
                .clipped()
                .frame(width: 60, height: 60)
              Spacer()
            }
            .padding(.bottom, 24)
            
            Text(onboardingConfig[currentIndex].title)
              .fontWeight(.semibold)
              .font(.system(size: 34))
              .foregroundColor(Color("primary-text"))
              .frame(maxWidth: .infinity, alignment: .leading)
              .padding(.bottom, 16)
            
            Text(onboardingConfig[currentIndex].message)
              .fontWeight(.regular)
              .font(.system(size: 22))
              .foregroundColor(Color("primary-text"))
              .frame(maxWidth: .infinity, alignment: .leading)
            
          }
          .padding(.top, 65)
          .padding(.horizontal, 36)
          
          Spacer()
          
          HStack{
            if currentIndex != 0{
              Button{
                previousButtonOnTap()
              } label: {
                Text("Previous")
                .fontWeight(.medium)
                .foregroundColor(Color("brand-primary"))
              }
              
            }
            Spacer()
            Button{
              nextButtonOnTap()
            }label: {
              Text(
                currentIndex+1 == onboardingConfig.count ?
                "Start":"Continue"
              )
              .fontWeight(.medium)
              .foregroundColor(Color("brand-primary"))
            }
              
          }
          .padding(.horizontal, 24)
          .padding(.bottom, 24)
        }
      }
      .cornerRadius(20)
      .frame(width: UIScreen.main.bounds.width-32)
      .padding(.top, 70)
      .padding(.vertical, 30)
    }
  }
}

