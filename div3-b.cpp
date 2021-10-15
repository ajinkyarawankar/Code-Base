#include <bits/stdc++.h> 
using namespace std; 
  
int main() 
{ 
    // added the two lines below 
    ios_base::sync_with_stdio(false); 
    cin.tie(NULL); 
    int t,n,ans,temp;
    cin>>t;
    while(t--){
        ans = 0;
        cin>>n;
        vector<int> a(3,0);
        for(int i=0;i<n;i++){
            cin>>temp;
            a[temp%3]++;
        }
        while(a[0]!=a[1] || a[1]!=a[2] || a[0]!=a[2]){
            if(a[0]>a[1]){
                temp = max(1,abs(a[0]-a[1])/2);
                ans += temp;
                a[0] -= temp;
                a[1] += temp;
            }
            if(a[1]>a[2]){
                temp = max(1,abs(a[1]-a[2])/2);
                ans += temp;
                a[1] -= temp;
                a[2] += temp;
            }
            if(a[2]>a[0]){
                temp = max(1,abs(a[2]-a[0])/2);
                ans += temp;
                a[2] -= temp;
                a[0] += temp;
            }
        }
        cout<<ans<<"\n";
    }
}