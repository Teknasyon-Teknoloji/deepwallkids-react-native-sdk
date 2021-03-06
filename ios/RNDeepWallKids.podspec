
Pod::Spec.new do |s|
  s.name         = "RNDeepWallKids"
  s.version      = "1.0.1"
  s.summary      = "RNDeepWallKids"
  s.description  = <<-DESC
                  RNDeepWallKids
                   DESC
  s.homepage     = "https://deepwall.com"
  s.license      = "MIT"
  s.author       = { "Deepwall" => "https://deepwall.com" }
  s.platform     = :ios, "10.0"
  s.source       = { :git => "https://github.com/Teknasyon-Teknoloji/deepwallkids-react-native-sdk.git", :tag => s.version }
  s.source_files  = "**/*.{h,m}"
  s.requires_arc = true
  s.swift_version = '5.1'

  s.static_framework = true

  s.dependency "React"
  s.dependency "DeepWallKids", '2.3.0'
end
