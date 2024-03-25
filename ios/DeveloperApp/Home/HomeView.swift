//
//  HomeSwiftUIView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 21/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI
import AVFoundation

struct HomeView: View {
  // Textfield Constant Label
  let label: String = "http://runtime-url:8080"
  
  // Launching function will be handled in HomeViewController.
  let launchOnTap: () -> Void
  
  init(launchOnTap:@escaping () -> Void) {
    self.launchOnTap = launchOnTap
  }
  
  // Home URL Textfield focus variable
  @State private var textFieldFocused: Bool = false
  // Showing Camera Screen
  @State private var showingCamera = false
  // Showing Camera Permission Alert
  @State private var showPermissionDeniedAlert = false
  
  // AppState as Observed Object
  @ObservedObject var appState: AppState = AppState.shared
  
  var body: some View {
    if showingCamera {
      CameraView(
        stopCamera: {
          showingCamera = false
        },
        onScanned: { val in
          appState.url = val
        }
      )
    } else {
      HomeBody
    }
  }
  
  var HomeBody: some View {
    VStack{
      LargeLogoImage(showVersion: false)
      
      Spacer().frame(height: 20)
      VStack{
        HStack{
          TextField(label, text: $appState.url, onEditingChanged: {(editingChanged) in
            if editingChanged {
              textFieldFocused = true
            } else {
              textFieldFocused = false
            }
          })
          .keyboardType(.URL)
          .autocapitalization(.none)
          .autocorrectionDisabled()
          .padding(.all, 8)
          .overlay(
            RoundedRectangle(cornerRadius: 4)
              .stroke(lineWidth: 1)
              .fill(Color("gray-primary"))
          )
          Image("qr_scan_icon")
            .resizable()
            .frame(width: 40, height: 40)
            .onTapGesture(perform: {
              requestCameraAccess {
                showingCamera = true
              }
            })
            .alert(isPresented: $showPermissionDeniedAlert){
              permissionDeniedAlert()
            }
          
        }.padding(.horizontal, 5)
          .padding(.bottom, 20)
        
        Divider()
          .foregroundColor(Color("gray-primary"))
        
        Toggle("Clear Cache", isOn: $appState.clearCache)
          .padding(.horizontal, 5)
        
        Spacer().frame(height: 5)
        
        Toggle("Developer Mode", isOn: $appState.developerMode)
          .padding(.horizontal, 5)
        
        // If the keyboard is presented, add small space.
        // Else, button should be at the bottom.
        if textFieldFocused{
          Spacer()
            .frame(height: 25)
        }else{
          Spacer()
        }
        
        Button {
          launchOnTap()
        } label: {
          if appState.isLoading(){
            ProgressView()
              .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
              .padding(.vertical, 12)
              .frame(maxWidth: .infinity)
              .background(Color("brand-primary"))
              .cornerRadius(8)
          }else{
            Text("Launch App")
              .foregroundColor(.white)
              .padding(.vertical, 12)
              .frame(maxWidth: .infinity)
              .background(appState.isUrlEmpty() ? Color("gray-primary") : Color("brand-primary"))
              .cornerRadius(8)
          }
        }
        // If loading or URL Textfield is empty, disable Launch Button.
        .disabled(appState.isLoading() || appState.isUrlEmpty())
        // If the keyboard is presented, do not add bottom tab bar padding.
        .padding(.bottom, textFieldFocused ? 0 : TabViewController().tabBarHeight + 50)
        
      }
      .padding(.horizontal,16)
    }
    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    .edgesIgnoringSafeArea(.bottom)
    .edgesIgnoringSafeArea(.top)
  }
  
  
  private func requestCameraAccess(complete: @escaping () -> Void){
    AVCaptureDevice.requestAccess(for: .video) { success in
      if success {
        DispatchQueue.main.async {
          complete()
        }
        return
      }
      showPermissionDeniedAlert = true
    }
  }
  
  func permissionDeniedAlert() -> Alert {
    return Alert(title: Text("Camera"), message: Text("Camera access is required to use the QR scanner"), primaryButton: .default(Text("OK")){
      UIApplication.shared.open(URL(string: UIApplicationOpenSettingsURLString)!)
    },secondaryButton: .cancel{
      showPermissionDeniedAlert = false
    })
  }
}

