require "open3"

def runCode(code)
  o, s = Open3.capture2("")
  o.strip
end

describe "numbers" do
  it "adds correctly" do
    expect(1+1).to eq(2)
  end
end