//
//  CameraView.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 23/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI
import CodeScanner
import AVFoundation

struct CameraView: View {
  let stopCamera: () -> Void // Exists Camera View.
  let onScanned: (_ url: String) -> Void // Scanner onScan callback.
  let simulatorData = "http://localhost:8080/" // Simulator Dummy Data
  
  var body: some View {
    ZStack(alignment: .topLeading){
      CodeScannerView(codeTypes: [.qr], simulatedData: simulatorData){ result in
        do{
          let res = try result.get().string
          stopCamera()
          onScanned(res)
        }
        catch{
          print("Error on scanning QR")
        }
      }
      
      VStack{
        HStack{
          Text("Cancel")
            .fontWeight(.semibold)
            .foregroundColor(Color.white)
            .onTapGesture {
              stopCamera()
            }
          Spacer()
        }
        
        Spacer().frame(height: 30)
        
        Text("Scan the QR code in StudioPro")
          .fontWeight(.regular)
          .foregroundColor(Color.black)
          .padding(.all, 16)
          .frame(alignment: .center)
          .background(Color("gray-primary"))
          .cornerRadius(4)
        
      }
      .padding(.top, 55)
      .padding(.horizontal,18)
    }
  }
}
