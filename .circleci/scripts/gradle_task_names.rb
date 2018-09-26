#!/usr/bin/env ruby

class String
  def camelize
    if empty?
      self
    else
      self[0].upcase + self[1,length-1]
    end
  end
end

flavor_name=ENV.fetch("ANDROID_FLAVOR_NAME")
build_type=ENV.fetch("ANDROID_BUILD_TYPE")

fb=flavor_name.camelize + build_type.camelize

puts Dir.glob("**/build.gradle").reject { |f| f == "build.gradle" }.map do |f|
  module_name=":#{File.dirname(f).gsub("/", ":")}"

  if File.foreach(f).grep(/(?:com\.android\.library|com\.android\.application)/).empty?
    # java library
    "#{module_name}:jar"
  else
    "#{module_name}:assemble#{fb}"
  end
end.join(" ")