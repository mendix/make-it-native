//
//  SmallLogoImage.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 21/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI

struct SmallLogoImage: View {
    var body: some View {
      Image("Logo Icon")
        .resizable()
        .scaledToFill()
        .frame(width: 32, height: 32)
        .clipped()
        .padding(.vertical, 8)
    }
}

struct SmallLogoImage_Previews: PreviewProvider {
    static var previews: some View {
        SmallLogoImage()
    }
}
